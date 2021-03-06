package com.example.makeze.dbmeter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import java.security.Provider;

public class LocationCoordinatesService extends Service {

    private Double lat;
    private Double lon;
    private String provider;

    private final IBinder binder = new LocationCoordinatesBinder();
    private static Location lastLocation = null;

    public class LocationCoordinatesBinder extends Binder {
        LocationCoordinatesService getLocationService() {
            return LocationCoordinatesService.this;
        }
    }

    @Override
    public void onCreate() {
        LocationListener listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation == null) {
                }
                lastLocation = location;
                lat = location.getLatitude();
                lon = location.getLongitude();
            }

            @Override
            public void onProviderDisabled(String arg0) {
            }

            @Override
            public void onProviderEnabled(String arg0) {
            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle bundle) {
            }
        };
            LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            try {
                provider = locManager.getBestProvider(new Criteria(), false);
                //provider = LocationManager.GPS_PROVIDER;
                if(provider!=null) {
                    locManager.requestLocationUpdates(provider, 1000, 1, listener);
                } else {
                    provider = "gps";
                }
            } catch (SecurityException e){
                System.out.println("NO PERMISSION FOR LOCATION");
            }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public double getLatitude(){
        System.out.println(lat+" !! "+lon);
        if(lat!=null)
            return lat;
        else return 404;
    }

    public double getLongitude(){
        System.out.println(lat+" !! "+lon);
        if(lon!=null)
            return lon;
        else return 404;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopSelf();
    }


}
