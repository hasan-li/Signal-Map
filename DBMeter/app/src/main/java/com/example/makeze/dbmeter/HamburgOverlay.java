package com.example.makeze.dbmeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.makeze.dbmeter.R.id.map;

public class HamburgOverlay extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener, OnMapReadyCallback, GoogleMap.OnGroundOverlayClickListener {

    private GoogleMap mMap;
    //hhOne
    private static final LatLng SW1 = new LatLng(53.51313, 9.89507); //bottom right corner of the image
    private static final LatLng NE2 = new LatLng(53.59392, 10.12355); //top left corner of the image

    private static final LatLng SOUTH_WEST = new LatLng(53.45215, 9.66556); //bottom right corner of the image
    private static final LatLng NORTH_EAST = new LatLng(53.67189, 10.2753); //top left corner of the image

    private final List<BitmapDescriptor> overlayImage = new ArrayList<BitmapDescriptor>();
    private GroundOverlay mGroundOverlay;
    //private ArrayList<LatLng> cellNetworkMap = new ArrayList<LatLng>();

    private static final int TRANSPARENCY_MAX = 100;
    private SeekBar mTransparencyBar;
    double latitude;
    double longitude;

    LocationCoordinates locationCoordinates;

    private Thread loadMaps;
    private Thread loadCellNetwork;


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

       // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng currentLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("LAT:" + latitude + " LNG:" + longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        mMap.setBuildingsEnabled(true);
        mMap.setMaxZoomPreference(16);
        //mMap.setMinZoomPreference(9);


        LatLngBounds bounds = new LatLngBounds(SOUTH_WEST,NORTH_EAST);

        mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(overlayImage.get(0))
                .positionFromBounds(bounds)
                .transparency(0.2f));

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

    public void toggleMap(View view) {
        if (mGroundOverlay != null) {
            mGroundOverlay.setClickable(((CheckBox) view).isChecked());
        }

    }

}
