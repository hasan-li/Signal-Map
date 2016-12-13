package com.example.makeze.dbmeter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class mainMapActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnGroundOverlayClickListener {

    private static final int COARSE_PERMISSION_REQUEST_CODE = 2;
    private GoogleMap mMap;
    LocationCoordinates locationCoordinates;
    double latitude;
    double longitude;

    //hhOne
    private static final LatLng SW1 = new LatLng(53.51313, 9.89507); //bottom right corner of the image
    private static final LatLng NE1 = new LatLng(53.59392, 10.12355); //top left corner of the image

    //hhTwo
    private static final LatLng SW2 = new LatLng(53.59413, 9.89593); //bottom right corner of the image
    private static final LatLng NE2 = new LatLng(53.68572, 10.12321); //top left corner of the image

    //bighh
    private static final LatLng SOUTH_WEST = new LatLng(53.45215, 9.66556); //bottom right corner of the image
    private static final LatLng NORTH_EAST = new LatLng(53.67189, 10.2753); //top left corner of the image

    private final List<BitmapDescriptor> overlayImages = new ArrayList<BitmapDescriptor>();
    private GroundOverlay mGroundOverlay1;
    private GroundOverlay mGroundOverlay2;
    //private ArrayList<LatLng> cellNetworkMap = new ArrayList<LatLng>();

    private static final int TRANSPARENCY_MAX = 100;
    private SeekBar mTransparencyBar;

    //signal strength vars

    //private SignalStrengthService signalService;
    //public int value = 0;

    //server update vars

    private UpdateServerService serverUpdate;
    private boolean signalBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // create a folder for storage
        setContentView(R.layout.activity_main_map);
        checkPermission();

        Button mainMenuButton = (Button) findViewById(R.id.mainMenuButton);
        mainMenuSetup(mainMenuButton);

        locationCoordinates = new LocationCoordinates(mainMapActivity.this);

        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //fetchFileTree intent starter
        Intent intent = new Intent(this, FetchFileTree.class);
        startActivity(intent);
        updateServerr();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serverUpdateIntent = new Intent(this, UpdateServerService.class);
        bindService(serverUpdateIntent, connectionToServerUpdateIntent, Context.BIND_AUTO_CREATE);
    }


    private void updateServerr(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(serverUpdate!=null){
                    try {
                        serverUpdate.updateServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Updating server! MAKEZE");
                handler.postDelayed(this,10000);
            }

        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnGroundOverlayClickListener(this);

        latitude = locationCoordinates.getLatitude();
        longitude = locationCoordinates.getLongitude();

        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng currentLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("LAT:" + latitude + " LNG:" + longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
        mMap.setBuildingsEnabled(true);
        mMap.setMaxZoomPreference(16);
        //mMap.setMinZoomPreference(9);

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        overlayImages.clear();
        overlayImages.add(BitmapDescriptorFactory.fromResource(R.drawable.hh_one));
        LatLngBounds bound1 = new LatLngBounds(SW1,NE1);
        mGroundOverlay1 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(overlayImages.get(0))
                .positionFromBounds(bound1)
                .transparency(0.2f));

        mTransparencyBar.setOnSeekBarChangeListener(this);
        mMap.setContentDescription("Google Map with ground overlay.");
    }


    // stuff for main menu goes here
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.mainMenuHeader);
        menu.add(0, v.getId(), 0, "Signal Stength");
        menu.add(0, v.getId(), 0, "Where am I");
        menu.add(0, v.getId(), 0, "Show good signal");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Signal Stength") {
            Intent signalStrengthIntent = new Intent(getApplicationContext(),HamburgOverlay.class);
            startActivity(signalStrengthIntent);
        } else if (item.getTitle() == "Where am I") {
            latitude = locationCoordinates.getLatitude();
            longitude = locationCoordinates.getLongitude();
            String currentCoordinates = "LAT:"+latitude+" LNG:"+longitude;
            Toast.makeText(this, "Fetching coordinates", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, currentCoordinates, Toast.LENGTH_LONG).show();
        } else if (item.getTitle() == "Show good signal") {
            Toast.makeText(this, "Generating overlay", Toast.LENGTH_SHORT).show();
            Intent HamburgOverlayIntent = new Intent(getApplicationContext(),HamburgOverlay.class);
            startActivity(HamburgOverlayIntent);
        } else {
            return false;
        }
        return true;
    }

    private void mainMenuSetup(View v){
        registerForContextMenu(v);
    }

    // stuff for locaiton service goes here
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

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
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mGroundOverlay1 != null) {
            mGroundOverlay1.setTransparency((float) progress / (float) TRANSPARENCY_MAX);
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
    }

    public void checkPermission() {
        Log.i("PERMISSION LOG", "Requesting telephony permissions.");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, COARSE_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, true);
        } else {
            Log.i("PERMISSION LOG", "Telephony permission been granted.");
        }
    }

    private ServiceConnection connectionToServerUpdateIntent = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            UpdateServerService.UpdateServerBinder updateServerBinder =
                    (UpdateServerService.UpdateServerBinder) binder;
            serverUpdate = updateServerBinder.updateTrigger();
            signalBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            signalBound = false;
        }
    };

}
