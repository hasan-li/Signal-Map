package com.example.mapdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Nilam on 08-Nov-16.
 */

public class LocationCoordinates extends Activity implements LocationListener {

    Context mContext;

    //flag for provider status
    public boolean isProviderEnabled = false;

    //flag for location available
    boolean locationStatus = false;

    Location location; //location
    public double lat; //latitude
    public double lng; //longitude

    //here comes the location manager
    LocationManager locationManager;
    String provider;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;


    public LocationCoordinates(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }


    public Location getLocation() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            provider = locationManager.getBestProvider(new Criteria(), false);
            isProviderEnabled = locationManager.isProviderEnabled(provider);
            Log.i("isProviderEnabled", "=" + isProviderEnabled);

            if (isProviderEnabled == false) {
            } else {
                this.locationStatus = true;

                if (isProviderEnabled) {
                    if (ActivityCompat.checkSelfPermission((Activity) mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((Activity) mContext,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }

                    locationManager.requestLocationUpdates(provider, 400, 1, this);
                    location = locationManager.getLastKnownLocation(provider);
                    onLocationChanged(location);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();

        }
        // return latitude
        return lat;
    }

    public double getLongitude() {
        if (location != null) {
            lng = location.getLongitude();
        }
        // return longitude
        return lng;
    }

    public boolean canGetLocation() {
        return this.locationStatus;
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("Latitude", "status");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("Latitude", "enable");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("Latitude", "disable");
    }

}