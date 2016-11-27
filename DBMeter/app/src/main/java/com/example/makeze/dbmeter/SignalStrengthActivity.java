package com.example.makeze.dbmeter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class SignalStrengthActivity extends AppCompatActivity {
    final int REQUEST_COARSE_LOCATION = 0;
    private TelephonyManager tm;
    private static final int COARSE_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_strength);
        showStrength();
    }

    public void showStrength() {
        Log.i("PERMISSION LOG", "Requesting telephony permissions.");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, COARSE_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, true);
        } else {
            Log.i("PERMISSION LOG",
                    "Telephony permission been granted.");
            showSignalStrength();
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
        Toast.makeText(this, "Fetching signal strength: "+allCellInfo.get(0).toString(), Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Fetching signal strength: "+allCellInfo.get(1).toString(), Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Fetching signal strength: "+allCellInfo.get(2).toString(), Toast.LENGTH_LONG).show();
        return allCellInfo.get(0).toString();
    }

}
