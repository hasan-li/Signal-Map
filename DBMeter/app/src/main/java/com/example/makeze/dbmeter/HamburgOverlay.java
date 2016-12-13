package com.example.makeze.dbmeter;

import android.*;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.makeze.dbmeter.R.id.map;

public class HamburgOverlay extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener, OnMapReadyCallback, GoogleMap.OnGroundOverlayClickListener {

    private GoogleMap mMap;
    //hhOne
    private static final LatLng SW1 = new LatLng(53.51313, 9.89507); //bottom right corner of the image
    private static final LatLng NE1 = new LatLng(53.59392, 10.12355); //top left corner of the image

    private static final LatLng SOUTH_WEST = new LatLng(53.45215, 9.66556); //bottom right corner of the image
    private static final LatLng NORTH_EAST = new LatLng(53.67189, 10.2753); //top left corner of the image

    private BitmapDescriptor Image;
    private GroundOverlay mGroundOverlay;

    private static final int TRANSPARENCY_MAX = 100;
    private SeekBar mTransparencyBar;


    LocationCoordinates locationCoordinates;
    LatLng currentLocation;
    double latitude;
    double longitude;

    private int READ_EXTERNAL_STORAGE_CODE = 5;

    String[] fileNameExtracted;
    String imageName;
    File dir;
    public boolean imageFoundInDirectory = false;
    String imageToOverlay = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hamburg_overlay);

        locationCoordinates = new LocationCoordinates(HamburgOverlay.this);
        latitude = locationCoordinates.getLatitude();
        longitude = locationCoordinates.getLongitude();
        currentLocation = new LatLng(latitude, longitude);

        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);


        //File dir = new File("/storage/emulated/0/DBMeter/"); // create your own directory name and read it instead
        dir = new File(Environment.getExternalStorageDirectory()+"/DBMeter");
        System.out.println("dir: "+dir);

        // have the object build the directory structure, if needed.
        if(!dir.exists()) {
            //directory is created
            dir.mkdirs();
        }

        showTreePerm(dir);
        //String path = dir.getAbsolutePath(); // get absolute path


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);

        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnGroundOverlayClickListener(this);

        Image = BitmapDescriptorFactory.fromResource(R.drawable.bighh);

       // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("LAT:" + latitude + " LNG:" + longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        mMap.setBuildingsEnabled(true);
        mMap.setMaxZoomPreference(16);
        //mMap.setMinZoomPreference(9);

        LatLngBounds bounds = new LatLngBounds(SOUTH_WEST,NORTH_EAST);

        mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(Image)
                .positionFromBounds(bounds)
                .transparency(0.8f));

        if(imageFoundInDirectory){
            System.out.println("TEST: DRAWING OVERLAY "+imageFoundInDirectory);
            Toast.makeText(getApplicationContext(),"Image found in app directory.", Toast.LENGTH_LONG).show();
            Image = BitmapDescriptorFactory.fromPath(dir+"/"+imageToOverlay);
            mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                    .image(Image)
                    .position(currentLocation, 8000f, 6000f)
                    .transparency(0.1f));
        }
        else{
            System.out.println("TEST: DOWNLOADING IMAGE "+imageFoundInDirectory);
            Toast.makeText(getApplicationContext(),"Image is downloading from server", Toast.LENGTH_LONG).show();
        }

        mTransparencyBar.setOnSeekBarChangeListener(this);
    }



    public void showTree(File dir){
        File files[] = dir.listFiles();

        for(File f : files){
            String filePath= f.getPath();
            System.out.println("FILENAMES: "+f.getPath());

            imageName=filePath.substring(filePath.lastIndexOf("/")+1);
            System.out.println("imageName: "+imageName);

            Pattern p = Pattern.compile("(.*?).png");
            Matcher m = p.matcher(imageName);

            while (m.find()) {

                //System.out.println("FILE "+m.group(1));
                String tempName = m.group(1);
                fileNameExtracted = tempName.split("_");
                System.out.println("fileNameExtracted: "+ Arrays.toString(fileNameExtracted));
            }

            if(latitude >= Double.parseDouble(fileNameExtracted[0]) && latitude <= Double.parseDouble(fileNameExtracted[2])
                    && longitude >= Double.parseDouble(fileNameExtracted[1]) && longitude <= Double.parseDouble(fileNameExtracted[3])){

                imageFoundInDirectory = true;
                imageToOverlay = fileNameExtracted[0]+"_"+fileNameExtracted[1]+"_"+fileNameExtracted[2]+"_"+fileNameExtracted[3]+".png";
                System.out.println("TEST IMAGE FOUND: "+imageFoundInDirectory);
                System.out.println("TEST Hamburg Overlay found "+ Arrays.toString(fileNameExtracted));
            /*
                overlayImage = BitmapDescriptorFactory.fromPath(dir+"/"+imageName);
                mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                        .image(overlayImage)
                        .position(currentLocation, 4300f, 3025f)
                        .transparency(0.2f));
            */

            }
            else{
                System.out.println("TEST IMAGE NOT FOUND. DOWNLOAD IT.");
                System.out.println("TEST Hamburg Overlay NOT found: "+ Arrays.toString(fileNameExtracted));

            }

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
            showTree(dir);

        }
    }


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
