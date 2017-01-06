package com.example.makeze.dbmeter;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Button;

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
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class mainMapActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnGroundOverlayClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback{

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
    private LatLng SW ; //bottom right corner of the image
    private LatLng NE ; //top left corner of the image

    private BitmapDescriptor image;
    private GroundOverlay mGroundOverlay;

    private int READ_EXTERNAL_STORAGE_CODE = 5;

    File dir;
    public boolean imageFoundInDirectory = false;
    String[] fileNameExtracted;
    String imageName;
    String imageToOverlay= null;
    String oldImageToOverlay= "";

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
        System.out.println("dir: "+dir);

        // have the object build the directory structure, if needed.
        if(!dir.exists()) {
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

    private void updateServer(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (locationService != null && signalService != null) {
                    String latTrim = String.format("%.6f", locationService.getLatitude()).replace(',','.');
                    String lonTrim = String.format("%.6f", locationService.getLongitude()).replace(',','.');

                    signalService.getSignalStrengthDBm();
                    String params = "x="+latTrim+
                            "&y="+lonTrim+
                            "&s="+signalService.getSignalStrengthDBm(); // http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?x=XXX.XXXXXX&y=YYY.YYYYYY&s=ZZZ
                    if(latTrim!="404.000000"){
                        //new LinkDownloaderClass(params).execute();
                        new UploaderClass(params).execute();
                    }
                    //new ImageDownloaderClass().execute();
                }
                handler.postDelayed(this, 10000);
            }
        });
    }

    private void getImage(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (locationService != null && signalService != null) {
                    String latTrim = String.format("%.6f", locationService.getLatitude()).replace(',','.');
                    String lonTrim = String.format("%.6f", locationService.getLongitude()).replace(',','.');

                    signalService.getSignalStrengthDBm();
                    String params = "x="+latTrim+
                            "&y="+lonTrim; // http://r1482a-02.etech.haw-hamburg.de/~w16cpteam1/cgi-bin/index?x=XXX.XXXXXX&y=YYY.YYYYYY
                    if(params!="x=404.000000&y=404.000000"){
                        new LinkDownloaderClass(params).execute();
                    }
                }
                handler.postDelayed(this, 10000);
            }
        });
    }


    private void makeOverlay(){

        final Handler overlayHandler = new Handler();

        overlayHandler.post(new Runnable() {

            @Override
            public void run()
            {
                System.out.println("DEBUG: Handler running");
                LatLngBounds bound;

                File files[] = dir.listFiles();

                for(File f : files){
                    String filePath= f.getPath();
                    System.out.println("FILENAMES: "+f.getPath());

                    imageName=filePath.substring(filePath.lastIndexOf("/")+1);
                    System.out.println("imageName: "+imageName);

                    Pattern p = Pattern.compile("(.*?).bmp");
                    Matcher m = p.matcher(imageName);

                    while (m.find()) {

                        String tempName = m.group(1);
                        fileNameExtracted = tempName.split("_");
                        System.out.println("fileNameExtracted: "+ Arrays.toString(fileNameExtracted));
                    }

                    double tempLatitude = locationService.getLatitude();
                    double tempLongitude = locationService.getLongitude();

                    if(tempLatitude != latitude){
                        latitude = tempLatitude;
                        if(tempLongitude != longitude){
                            longitude = tempLongitude;
                        }
                    }

                    currentLocation = new LatLng(latitude, longitude);

                    try{
                        if (latOne == Double.parseDouble(fileNameExtracted[0]) && latTwo == Double.parseDouble(fileNameExtracted[2])
                                && lngOne == Double.parseDouble(fileNameExtracted[1]) && lngTwo == Double.parseDouble(fileNameExtracted[3])) {

                            imageFoundInDirectory = true;
                            imageToOverlay = fileNameExtracted[0] + "_" + fileNameExtracted[1] + "_" + fileNameExtracted[2] + "_" + fileNameExtracted[3] + ".bmp";
                            System.out.println("DEBUG: Hamburg Overlay found for: " + Arrays.toString(fileNameExtracted));
                            SW = new LatLng(latOne, lngOne);
                            NE = new LatLng(latTwo, lngTwo);
                            //oldImageToOverlay = imageToOverlay;
                        } else {
                            System.out.println("DEBUG: Hamburg Overlay NOT found for:  " + Arrays.toString(fileNameExtracted));

                        }
                    }catch(Exception e){
                        Log.i("DEBUG", "Checking Image");
                    }

                }

                System.out.println("DEBUG: Image name should be: " + latOne+"_"+lngOne+"_"+latTwo+"_"+lngTwo);

                try{
                    //coord. names are changed to image name. if the image name is different, go to this loop.
                    if (!oldImageToOverlay.equals(imageToOverlay)) {
                        //if the image is in directory, use that image for overlay
                        if (imageFoundInDirectory) {
                            System.out.println("DEBUG: DRAWING OVERLAY " + imageFoundInDirectory + " with image: " + imageToOverlay);
                            Toast.makeText(getApplicationContext(), "Image found.", Toast.LENGTH_LONG).show();

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
                            System.out.println("DEBUG: DOWNLOADING IMAGE ");
                            Toast.makeText(getApplicationContext(), "Waiting for an image...", Toast.LENGTH_LONG).show();
                        }

                    }
                }catch(Exception e){
                    Log.i("DEBUG", "image name error");
                }

                overlayHandler.postDelayed(this, 10000);
            }
        });
    }


    private void whereAmI(){

        if (locationService != null && locationService.getLatitude() != 404 && locationService.getLongitude() != 404) {
            currentLocation = new LatLng(locationService.getLatitude(), locationService.getLongitude());
        }
        else {
            currentLocation = new LatLng(locationCoordinates.getLatitude(), locationCoordinates.getLongitude());
        }

        mMap.addMarker(new MarkerOptions().position(currentLocation).title("LAT:" + latitude + " LNG:" + longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void showGoodSignal(){
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(latitude, longitude), new LatLng(53.555037, 10.022218))
                .width(10)
                .color(Color.RED));
        mMap.addMarker(new MarkerOptions().position(new LatLng(53.555037, 10.022218)).title("Assumed strong signal point"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        mMap.setBuildingsEnabled(true);
        mMap.setMaxZoomPreference(16);

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        mTransparencyBar.setOnSeekBarChangeListener(this);
    }


    public void latitudeApproximator() {

        if(latitude >= (Math.floor(latitude)+0.0) && latitude < (Math.floor(latitude)+0.3)){
            //System.out.println("TEST latAprox: "+latitude+" lies within 0 and 0.3");
            latOne = Math.floor(latitude)+0.0;
            latTwo = Math.floor(latitude)+0.3;
        }

        else if(latitude >= (Math.floor(latitude)+0.3) && latitude < (Math.floor(latitude)+0.6)){
            //System.out.println("TEST latAprox: "+latitude+" lies within 0.3 and 0.6");
            latOne = Math.floor(latitude)+0.3;
            latTwo = Math.floor(latitude)+0.6;
        }

        if(latitude >= (Math.floor(latitude)+0.6) && latitude < (Math.floor(latitude)+0.9999)){
            //System.out.println("TEST latAprox: "+latitude+" lies within 0.6 and 0.9999");
            latOne = Math.floor(latitude)+0.6;
            latTwo = Math.floor(latitude)+0.9;
        }
    }


    public void longitudeApproximator(){

        if(longitude >= (Math.floor(longitude)+0.0) && longitude < (Math.floor(longitude)+0.3)){
            //System.out.println("TEST lngAprox: "+longitude+" lies within 0 and 0.3");
            lngOne = Math.floor(longitude)+0.0;
            lngTwo = Math.floor(longitude)+0.3;
        }

        else if(longitude >= (Math.floor(longitude)+0.3) && longitude < (Math.floor(longitude)+0.6)){
            //System.out.println("TEST lngAprox: "+longitude+" lies within 0.3 and 0.6");
            lngOne = Math.floor(longitude)+0.3;
            lngTwo = Math.floor(longitude)+0.6;
        }

        if(longitude >= (Math.floor(longitude)+0.6) && longitude < (Math.floor(longitude)+0.9999)){
            //System.out.println("TEST lngAprox: "+longitude+" lies within 0.6 and 0.9999");
            lngOne = Math.floor(longitude)+0.6;
            lngTwo = Math.floor(longitude)+0.9;
        }
    }


    public void showTreePerm(File dir){
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
            makeOverlay();
        /*} else if (item.getTitle() == "Where am I") {
            latitude = locationCoordinates.getLatitude();
            longitude = locationCoordinates.getLongitude();
            String currentCoordinates = "LAT:"+latitude+" LNG:"+longitude;
            Toast.makeText(this, "Marking your location", Toast.LENGTH_SHORT).show();
            whereAmI();*/
        } else if (item.getTitle() == "Point to best signal location") {
            //Toast.makeText(this, "Generating overlay", Toast.LENGTH_SHORT).show();
            showGoodSignal();
        } else {
            return false;
        }
        return true;
    }

    private void mainMenuSetup(View v){
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
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
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
}
