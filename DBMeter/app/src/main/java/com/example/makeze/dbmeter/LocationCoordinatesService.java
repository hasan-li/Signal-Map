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

    private final IBinder binder = new LocationCoordinatesBinder();
    private static Location lastLocation = null;
    private static double distanceInMeters;

    public class LocationCoordinatesBinder extends Binder {
        LocationCoordinatesService getLocation() {
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
                distanceInMeters += location.distanceTo(lastLocation);
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

            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        };
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
