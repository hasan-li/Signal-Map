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

    private BitmapDescriptor image;
    private GroundOverlay mGroundOverlay;

    private static final int TRANSPARENCY_MAX = 100;
    private SeekBar mTransparencyBar;


    LocationCoordinates locationCoordinates;
    LatLng currentLocation;
    double latitude;
    double longitude;
    private double latOne, latTwo;
    private double lngOne, lngTwo;
    private LatLng SW2 ; //bottom right corner of the image
    private LatLng NE2 ; //top left corner of the image
    //hhOne
    private static final LatLng SW1 = new LatLng(53.51313, 9.89507); //bottom right corner of the image
    private static final LatLng NE1 = new LatLng(53.59392, 10.12355); //top left corner of the image


    private int READ_EXTERNAL_STORAGE_CODE = 5;

    File dir;
    public boolean imageFoundInDirectory = false;
    String[] fileNameExtracted;
    String imageName;
    String imageToOverlay= "null";
    String oldImageToOverlay= "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hamburg_overlay);

        locationCoordinates = new LocationCoordinates(HamburgOverlay.this);
        latitude = locationCoordinates.getLatitude();
        longitude = locationCoordinates.getLongitude();
        currentLocation = new LatLng(latitude, longitude);

        latitudeApproximator();
        longitudeApproximator();

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

        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("LAT:" + latitude + " LNG:" + longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        mMap.setBuildingsEnabled(true);
        mMap.setMaxZoomPreference(16);


        //display this default overlay when there is no signal strength image to display
        LatLngBounds bound = new LatLngBounds(SW1, NE1);
        image = BitmapDescriptorFactory.fromResource(R.drawable.hh_one);
        mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(image)
                .positionFromBounds(bound)
                .transparency(0.9f));

        //coord. names are changed to image name. if the image name is different, go to this loop.
        if (!oldImageToOverlay.equals(imageToOverlay)) {

            //if the image is in directory, use that image for overlay
            if (imageFoundInDirectory) {
                System.out.println("TEST: DRAWING OVERLAY " + imageFoundInDirectory + " with image: " + imageToOverlay);
                Toast.makeText(getApplicationContext(), "Image found in app directory.", Toast.LENGTH_LONG).show();

                bound = new LatLngBounds(SW2, NE2);
                image = BitmapDescriptorFactory.fromPath(dir + "/" + imageToOverlay);
                mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                        .image(image)
                        .positionFromBounds(bound)
                        .transparency(0.1f));
            }
            //if it is not is directory, download the image
            else {
                System.out.println("TEST: DOWNLOADING IMAGE " + imageFoundInDirectory);
                Toast.makeText(getApplicationContext(), "Image is downloading from server", Toast.LENGTH_LONG).show();
            }

            oldImageToOverlay = imageToOverlay;

        }

        //if the image name is same, it means user is at the same place. display this overlay.
        else{

            bound = new LatLngBounds(SW2, NE2);
            image = BitmapDescriptorFactory.fromPath(dir + "/" + imageToOverlay);
            mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                    .image(image)
                    .positionFromBounds(bound)
                    .transparency(0.1f));

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

            Pattern p = Pattern.compile("(.*?).bmp");
            Matcher m = p.matcher(imageName);

            while (m.find()) {

                String tempName = m.group(1);
                fileNameExtracted = tempName.split("_");
                System.out.println("fileNameExtracted: "+ Arrays.toString(fileNameExtracted));
            }

            if(latOne == Double.parseDouble(fileNameExtracted[0]) && latTwo == Double.parseDouble(fileNameExtracted[2])
                    && lngOne == Double.parseDouble(fileNameExtracted[1]) && lngTwo == Double.parseDouble(fileNameExtracted[3])){

                imageFoundInDirectory = true;
                imageToOverlay = fileNameExtracted[0]+"_"+fileNameExtracted[1]+"_"+fileNameExtracted[2]+"_"+fileNameExtracted[3]+".bmp";
                System.out.println("TEST: Hamburg Overlay found for: "+ Arrays.toString(fileNameExtracted));
                SW2 = new LatLng(latOne, lngOne);
                NE2 = new LatLng(latTwo, lngTwo);
                //oldImageToOverlay = imageToOverlay;
            }
            else{
                System.out.println("TEST: Hamburg Overlay NOT found for:  "+ Arrays.toString(fileNameExtracted));

            }

        }

        System.out.println("TEST: Image coordinates are: " + latOne+"_"+lngOne+"_"+latTwo+"_"+lngTwo);
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

