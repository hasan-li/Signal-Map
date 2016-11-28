package com.example.makeze.dbmeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

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

import java.util.ArrayList;
import java.util.List;

import static com.example.makeze.dbmeter.R.id.map;

public class HamburgOverlay extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener, OnMapReadyCallback, GoogleMap.OnGroundOverlayClickListener {

    private GoogleMap mMap;
    //private static final LatLng SOUTH_WEST = new LatLng(53.51303, 9.89507); //bottom right corner of the image
    //private static final LatLng NORTH_EAST = new LatLng(53.59368, 10.12336); //top left corner of the image

    private static final LatLng SOUTH_WEST = new LatLng(53.45215, 9.66556); //bottom right corner of the image
    private static final LatLng NORTH_EAST = new LatLng(53.67189, 10.2753); //top left corner of the image

    LocationCoordinates locationCoordinates;

    private final List<BitmapDescriptor> overlayImage = new ArrayList<BitmapDescriptor>();
    public GroundOverlay mGroundOverlay;

    private static final int TRANSPARENCY_MAX = 100;
    private SeekBar mTransparencyBar;
    double latitude;
    double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hamburg_overlay);

        locationCoordinates = new LocationCoordinates(HamburgOverlay.this);

        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);

        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnGroundOverlayClickListener(this);

        latitude = locationCoordinates.getLatitude();
        longitude = locationCoordinates.getLongitude();

        overlayImage.clear();
        overlayImage.add(BitmapDescriptorFactory.fromResource(R.drawable.bighh));

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng currentLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("LAT:" + latitude + " LNG:" + longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        mMap.setBuildingsEnabled(true);
        mMap.setMaxZoomPreference(16);
        mMap.setMinZoomPreference(9);


        LatLngBounds bounds = new LatLngBounds(SOUTH_WEST,NORTH_EAST);

        mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(overlayImage.get(0))
                .positionFromBounds(bounds)
                .transparency(0.1f));

        mTransparencyBar.setOnSeekBarChangeListener(this);
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
