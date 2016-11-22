package com.example.makeze.dbmeter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class SignalStrengthActivity extends AppCompatActivity {
    final int REQUEST_COARSE_LOCATION = 0;
    private TelephonyManager tm;
    private Telephony tf;
    private SignalStrength ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_strength);
        showStrength();
    }

    public void showStrength() {
        Log.i("PERMISSION LOG", "Requesting telephony permissions.");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestTelephonyPermission();

        } else {
            Log.i("PERMISSION LOG",
                    "Telephony permission been granted.");
            showSignalStrength();
        }
    }

    private void requestTelephonyPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_COARSE_LOCATION);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showSignalStrength();

                } else {

                    System.out.print("NO PERMISSIOOO...");
                }
                return;
            }
        }
    }

    public String showSignalStrength() {
        // TODO: 11/15/16
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int n = tm.getNetworkType();
        System.out.print("Network type is: " + n + "\n");
        String n1 = tm.getNetworkOperatorName();
        System.out.print("Network op-r is: " + n1 + "\n");
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        // for example value of first element
        List<CellInfo> allCellInfo = telephonyManager.getAllCellInfo();
        Toast.makeText(this, "Fetching signal strength", Toast.LENGTH_SHORT).show();
        return allCellInfo.get(0).toString();
    }

}
