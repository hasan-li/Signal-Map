package com.example.mapdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CoordinatesTest extends AppCompatActivity{


    Button btnGetLocation;
    EditText editLocation;
    LocationCoordinates locationCoordinates;
    private static final String TAG = "Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates_test);

        editLocation = (EditText) findViewById(R.id.editTextLocation);
        editLocation.setText("Click the button below to" + " see the coordinates (latitude & longitude)");

        locationCoordinates=new LocationCoordinates(CoordinatesTest.this);
        btnGetLocation = (Button) findViewById(R.id.btnLocation);
        btnGetLocation.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                locate();
            }
        });

    }


    public void locate(){
        if(locationCoordinates.canGetLocation()) {
            // Toast.makeText(this, "LAT " + locationCoordinates.getLatitude() + " ,LNG " + locationCoordinates.getLongitude(), Toast.LENGTH_SHORT).show();
            //Log.i(TAG, "LAT " + locationCoordinates.getLatitude() + " LNG " + locationCoordinates.getLongitude());

            editLocation.setText("");
            //Toast.makeText(getBaseContext(),"Location changed : Lat: " +location.getLatitude()+ " Lng: " + location.getLongitude(),Toast.LENGTH_SHORT).show();

            String latitude = " Latitude: " +locationCoordinates.getLatitude();
            String longitude = " Longitude: " +locationCoordinates.getLongitude();

            String s = latitude+"\n"+longitude;
            editLocation.setText(s);
        }

        else{
            Log.i(TAG,"No location found");
            Toast.makeText(this, "No location found", Toast.LENGTH_LONG).show();
        }
    }


}
