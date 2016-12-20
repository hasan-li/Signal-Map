package com.example.makeze.dbmeter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class LocationCoordinatesService extends Service {

    private Double lat;
    private Double lon;

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
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);
            } catch (SecurityException e){
                System.out.println("NO PERMISSION FOR LOCATION");
            }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public double getLat(){
        System.out.println(lat+" !! "+lon);
        if(lat!=null)
            return lat;
        else return 404;
    }

    public double getLon(){
        System.out.println(lat+" !! "+lon);
        if(lon!=null)
            return lon;
        else return 404;
    }



}
