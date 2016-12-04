package com.example.makeze.dbmeter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class SignalStrengthActivity extends AppCompatActivity {
    private TelephonyManager mTelephonyManager;
    private static final int COARSE_PERMISSION_REQUEST_CODE = 2;
    MyPhoneStateListener mPhoneStateListener;
    private int mSignalStrength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_strength);
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        mPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        showStrength();
        Toast.makeText(this, "Fetching signal strength: "+mSignalStrength, Toast.LENGTH_SHORT).show();
    }

    public void showStrength() {
        Log.i("PERMISSION LOG", "Requesting telephony permissions.");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, COARSE_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, true);
        } else {
            Log.i("PERMISSION LOG", "Telephony permission been granted.");
            //showSignalStrength();
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            mSignalStrength = signalStrength.getGsmSignalStrength();
            mSignalStrength = signalStrength.getCdmaDbm();
            mSignalStrength = (2 * mSignalStrength) - 113; // -> dBm
            System.out.println("!!!!"+mSignalStrength);
            //Toast.makeText(this, "Fetching signal strength", Toast.LENGTH_SHORT).show();
        }
    }
}
