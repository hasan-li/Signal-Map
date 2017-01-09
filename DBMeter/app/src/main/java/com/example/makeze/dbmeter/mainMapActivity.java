package com.example.makeze.dbmeter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Location;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.os.Handler;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Color.BLUE;


public class mainMapActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnGroundOverlayClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private Context mContext;
    private static final int COARSE_PERMISSION_REQUEST_CODE = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int FINE_PERMISSION_REQUEST_CODE = 1;
    private static final int WRITE_PERMISSION_REQUEST_CODE = 3;

    private GoogleMap mMap;
    private LocationCoordinates locationCoordinates;
    private LatLng currentLocation;
    private double latitude;
    private double longitude;
    private double latOne, latTwo;
    private double lngOne, lngTwo;
    private LatLng SW; //bottom right corner of the image
    private LatLng NE; //top left corner of the image

    private BitmapDescriptor image;
    private GroundOverlay mGroundOverlay;
    private Marker routeMarker;
    private Polyline signalPointer;
    private Polyline route;

    private int READ_EXTERNAL_STORAGE_CODE = 5;

    private File dir;
    private boolean imageFoundInDirectory = false;
    private String[] fileNameExtracted;
    private String imageName;
    private String imageToOverlay = null;
    private String oldImageToOverlay = "";

    private static final int TRANSPARENCY_MAX = 100;
    private SeekBar mTransparencyBar;

    //signal strength vars
    private SignalStrengthService signalService;
    public int signalStrengthDBm = 0;

    private LocationCoordinatesService locationService;

    //server update vars
    private UpdateServerService serverUpdate;
    private boolean signalBound = false;
    private boolean locationBound = false;
    private boolean permissionsGranted = false;
    List<String> backUp = new ArrayList<String>();
    private int downloadFrequency = 10000;
    private int uploadFrequency = 10000;

    private UploaderClass serverUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.mContext = this;
        checkPermissions();
        // create a folder for storage
        setContentView(R.layout.activity_main_map);

        ImageButton mainMenuButton = (ImageButton) findViewById(R.id.mainMenuButton);
        mainMenuSetup(mainMenuButton);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationCoordinates = new LocationCoordinates(mainMapActivity.this);

        if (locationCoordinates.getLocation() == null) {
            alertbox();
        }

        latitude = locationCoordinates.getLatitude();
        longitude = locationCoordinates.getLongitude();
        currentLocation = new LatLng(latitude, longitude);

        latitudeApproximator();
        longitudeApproximator();

        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);

        dir = new File("/storage/emulated/0/DBMeter/"); // create your own directory name and read it instead
        //dir = new File(Environment.getExternalStorageDirectory()+"/DBMeter");
        System.out.println("dir: " + dir);

        // have the object build the directory structure, if needed.
        if (!dir.exists()) {
            dir.mkdirs();
        }

        showTreePerm(dir);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // intent service for signal strength
        Intent signalStrengthIntent = new Intent(this, SignalStrengthService.class);
        bindService(signalStrengthIntent, connectionToSignalStrengthIntent, Context.BIND_AUTO_CREATE);
        // intent service for coordinates
        Intent locationCoordinatesIntent = new Intent(this, LocationCoordinatesService.class);
        bindService(locationCoordinatesIntent, connectionToLocationCoordinatesIntent, Context.BIND_AUTO_CREATE);

        if (permissionsGranted) {
            updateServer();
            getImage();
        }
    }

    private void updateServer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (locationService != null && signalService != null) {
                    String latTrim = String.format("%.6f", locationService.getLatitude()).replace(',', '.');
                    String lonTrim = String.format("%.6f", locationService.getLongitude()).replace(',', '.');

                    signalService.getSignalStrengthDBm();
                    String params = "x=" + latTrim +
                            "&y=" + lonTrim +
                            "&s=" + signalService.getSignalStrengthDBm(); // http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?x=XXX.XXXXXX&y=YYY.YYYYYY&s=ZZZ
                    if (latTrim != "404.000000") {
                        //new LinkDownloaderClass(params).execute();
                        backUp.add("http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?" + params);
                        new UploaderClass(params).execute();
                    }
                    //new ImageDownloaderClass().execute();
                }
                handler.postDelayed(this, uploadFrequency);
            }
        });
    }

    private void getImage() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (locationService != null && signalService != null) {
                    String latTrim = String.format("%.6f", locationService.getLatitude()).replace(',', '.');
                    String lonTrim = String.format("%.6f", locationService.getLongitude()).replace(',', '.');

                    signalService.getSignalStrengthDBm();
                    String params = "x=" + latTrim +
                            "&y=" + lonTrim; // http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?x=XXX.XXXXXX&y=YYY.YYYYYY
                    if (params != "x=404.000000&y=404.000000") {
                        new LinkDownloaderClass(params).execute();
                    }
                }
                handler.postDelayed(this, downloadFrequency);
            }
        });
    }


    private void makeOverlay() {
        final Handler overlayHandler = new Handler();
        overlayHandler.post(new Runnable() {

            @Override
            public void run() {
                System.out.println("DEBUG: Handler running");
                LatLngBounds bound;

                FilenameFilter filter = new FilenameFilter() {
                    public boolean accept (File dir, String name) {
                        return name.endsWith(".bmp");
                    }
                };

                String[] file = dir.list(filter);
                if (file == null) {
                } else {
                    for (int i=0; i< file.length; i++) {
                        imageName = file[i];
                        System.out.println("DEBUG imageNames "+imageName);
                    }
                }

                    Pattern p = Pattern.compile("(.*?).bmp");
                    Matcher m = p.matcher(imageName);

                    while (m.find()) {
                        String tempName = m.group(1);
                        fileNameExtracted = tempName.split("_");
                        System.out.println("fileNameExtracted: " + Arrays.toString(fileNameExtracted));
                    }

                    double tempLatitude = locationService.getLatitude();
                    double tempLongitude = locationService.getLongitude();

                    if (tempLatitude != latitude || tempLongitude != longitude) {
                        latitude = tempLatitude;
                        longitude = tempLongitude;
                    }

                    currentLocation = new LatLng(latitude, longitude);

                    try {
                        if (latOne == Double.parseDouble(fileNameExtracted[0]) && latTwo == Double.parseDouble(fileNameExtracted[2])
                                && lngOne == Double.parseDouble(fileNameExtracted[1]) && lngTwo == Double.parseDouble(fileNameExtracted[3])) {
                            imageToOverlay = fileNameExtracted[0] + "_" + fileNameExtracted[1] + "_" + fileNameExtracted[2] + "_" + fileNameExtracted[3] + ".bmp";
                            imageFoundInDirectory = true;
                            System.out.println("DEBUG: Hamburg Overlay found for: " + Arrays.toString(fileNameExtracted));
                            SW = new LatLng(latOne, lngOne);
                            NE = new LatLng(latTwo, lngTwo);
                        }
                        else{
                            System.out.println("DEBUG: Hamburg Overlay NOT found for:  " + Arrays.toString(fileNameExtracted));
                            imageFoundInDirectory = false;
                        }
                    } catch (Exception e) {
                        Log.i("DEBUG", "Checking Image");
                    }

                System.out.println("DEBUG: Image name should be: " + latOne + "_" + lngOne + "_" + latTwo + "_" + lngTwo);

                try {
                    //coordinates names are changed to image name. if the image name is different, go to this loop.
                    if (!oldImageToOverlay.equals(imageToOverlay)) {
                        //if the image is in directory, use that image for overlay
                        if (imageFoundInDirectory) {
                            System.out.println("DEBUG: DRAWING OVERLAY " + imageFoundInDirectory + " with image: " + imageToOverlay);
                            Toast.makeText(getApplicationContext(), "Drawing Overlay", Toast.LENGTH_SHORT).show();

                            if (mGroundOverlay != null) {
                                mGroundOverlay.remove();
                            }

                            bound = new LatLngBounds(SW, NE);
                            image = BitmapDescriptorFactory.fromPath(dir + "/" + imageToOverlay);
                            mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                                    .image(image)
                                    .positionFromBounds(bound)
                                    .transparency(0.1f));

                            oldImageToOverlay = imageToOverlay;
                        }
                        //if it is not is directory, download the image
                        else {
                            Toast.makeText(getApplicationContext(), "Waiting for an image ....", Toast.LENGTH_SHORT).show();
                            if(mGroundOverlay != null){
                                mGroundOverlay.remove();
                            }
                            System.out.println("DEBUG: DOWNLOADING IMAGE ");
                        }

                    }
                    else{
                        if(mGroundOverlay != null && !imageFoundInDirectory){
                            mGroundOverlay.remove();
                        }
                    }

                } catch (Exception e) {
                    Log.i("DEBUG", "image name error");
                }

                overlayHandler.postDelayed(this, 10000);
            }
        });
    }


    private void whereAmI() {

        if (locationService != null && locationService.getLatitude() != 404 && locationService.getLongitude() != 404) {
            currentLocation = new LatLng(locationService.getLatitude(), locationService.getLongitude());
        } else {
            currentLocation = new LatLng(locationCoordinates.getLatitude(), locationCoordinates.getLongitude());
        }

        mMap.addMarker(new MarkerOptions().position(currentLocation).title("LAT:" + latitude + " LNG:" + longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void showGoodSignal() {
        signalPointer = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(latitude, longitude),
                        new LatLng(53.556769, 10.022707),
                        new LatLng (53.556303, 10.021608),
                        new LatLng(53.555437, 10.022772),
                        new LatLng(53.554831, 10.023201),
                        new LatLng(53.553690, 10.024086),
                        new LatLng(53.553228, 10.020905))
                .width(10)
                .color(Color.RED));
        routeMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(53.553228, 10.020905)).title("Assumed strong signal point"));
        signalPointer.setGeodesic(true);
    }


    //Method to create an AlertBox
    protected void alertbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Html.fromHtml((getString(R.string.alert_Msg))))
                .setCancelable(false)
                .setTitle(Html.fromHtml(("<b>" + getString(R.string.alert_title) + "</b>")))
                .setPositiveButton("DO IT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent myIntent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Location access denied", Toast.LENGTH_SHORT).show();
                        // cancel the dialog box
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(BLUE);
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(BLUE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.setBuildingsEnabled(true);
        mMap.setMaxZoomPreference(16);

        if (locationCoordinates.getLocation() == null) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(0));
        } else
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        mTransparencyBar.setOnSeekBarChangeListener(this);
    }


    public void latitudeApproximator() {

        if (latitude >= (Math.floor(latitude) + 0.0) && latitude < (Math.floor(latitude) + 0.3)) {
            latOne = Math.floor(latitude) + 0.0;
            latTwo = Math.floor(latitude) + 0.3;
        } else if (latitude >= (Math.floor(latitude) + 0.3) && latitude < (Math.floor(latitude) + 0.6)) {
            latOne = Math.floor(latitude) + 0.3;
            latTwo = Math.floor(latitude) + 0.6;
        }

        if (latitude >= (Math.floor(latitude) + 0.6) && latitude < (Math.floor(latitude) + 0.9999)) {
            latOne = Math.floor(latitude) + 0.6;
            latTwo = Math.floor(latitude) + 0.9;
        }
    }


    public void longitudeApproximator() {

        if (longitude >= (Math.floor(longitude) + 0.0) && longitude < (Math.floor(longitude) + 0.3)) {
            lngOne = Math.floor(longitude) + 0.0;
            lngTwo = Math.floor(longitude) + 0.3;
        } else if (longitude >= (Math.floor(longitude) + 0.3) && longitude < (Math.floor(longitude) + 0.6)) {
            lngOne = Math.floor(longitude) + 0.3;
            lngTwo = Math.floor(longitude) + 0.6;
        }

        if (longitude >= (Math.floor(longitude) + 0.6) && longitude < (Math.floor(longitude) + 0.9999)) {
            lngOne = Math.floor(longitude) + 0.6;
            lngTwo = Math.floor(longitude) + 0.9;
        }
    }


    public void showTreePerm(File dir) {
        Log.i("PERMISSION LOG", "Requesting telephony permissions.");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, READ_EXTERNAL_STORAGE_CODE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE, true);
        } else {
            Log.i("PERMISSION LOG", "Readstuff permission been granted.");
            // showTree(dir);
        }
    }


    // stuff for main menu goes here
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.mainMenuHeader);
        menu.add(0, v.getId(), 0, "Generate signal strength");
        //menu.add(0, v.getId(), 0, "Where am I");
        menu.add(0, v.getId(), 0, "Point to best signal location");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Generate signal strength") {
                if (mGroundOverlay != null) {
                    Toast.makeText(this, "Overlay removed", Toast.LENGTH_SHORT).show();
                    mGroundOverlay.remove();
                    mGroundOverlay = null;
                    oldImageToOverlay = "";
                } else {
                    makeOverlay();
                }

        /*} else if (item.getTitle() == "Where am I") {
            // Toast.makeText(this, "Marking your location", Toast.LENGTH_SHORT).show();
            if(locMarker != null){
                Toast.makeText(this, "Location Marker removed", Toast.LENGTH_SHORT).show();
                locMarker.remove();
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                locMarker = null;
            }else{
                whereAmI();
            }*/
        } else if (item.getTitle() == "Point to best signal location") {
            if (signalPointer != null) {
                Toast.makeText(this, "path removed", Toast.LENGTH_SHORT).show();
                signalPointer.remove();
                signalPointer = null;
                routeMarker.remove();
            } else {
                showGoodSignal();
            }
        }
        else {
            return false;
        }
        return true;
    }

    private void mainMenuSetup(View v) {
        registerForContextMenu(v);
    }

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */

    private boolean mPermissionDenied = false;


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        if((locationCoordinates.getLocation() == null) || (locationService.getLatitude() == 404 && locationService.getLongitude() == 404)){
            Toast.makeText(getApplicationContext(), "Location services not available", Toast.LENGTH_SHORT).show();
            return  true;
        } else{
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    public void checkPermissions() {
        Log.i("PERMISSION LOG", "Requesting telephony permissions.");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, WRITE_PERMISSION_REQUEST_CODE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
        } else {
            permissionsGranted = true;
            Log.i("PERMISSION LOG", "Telephony permission been granted.");
        }
    }

    private ServiceConnection connectionToSignalStrengthIntent = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            SignalStrengthService.SignalStrengthBinder signalStrengthBinder =
                    (SignalStrengthService.SignalStrengthBinder) binder;
            signalService = signalStrengthBinder.getSignal();
            signalBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            signalBound = false;
        }
    };

    private ServiceConnection connectionToLocationCoordinatesIntent = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            LocationCoordinatesService.LocationCoordinatesBinder locationCoordinatesBinder =
                    (LocationCoordinatesService.LocationCoordinatesBinder) binder;
            locationService = locationCoordinatesBinder.getLocationService();
            locationBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationBound = false;
        }
    };


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mGroundOverlay != null) {
            mGroundOverlay.setTransparency((float) progress / (float) TRANSPARENCY_MAX);
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {
        mGroundOverlay.setTransparency(0.5f - mGroundOverlay.getTransparency());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new BackUpClass(backUp);
    }
}
