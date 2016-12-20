package com.example.makeze.dbmeter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

public class SignalStrengthService extends Service {
    private final IBinder binder = new SignalStrengthBinder();
    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListener mPhoneStateListener;
    private int mSignalStrength = 0;

    public class SignalStrengthBinder extends Binder {
        SignalStrengthService getSignal() {
            return SignalStrengthService.this;
        }
    }

    public SignalStrengthService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate(){
        mPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //mSignalStrength = signalStrength.getGsmSignalStrength();
            mSignalStrength = signalStrength.();
            System.out.println("Here dragons are: "+mSignalStrength);
        }
    }

    public int getSignalStrengthDBm(){
        System.out.println("!! "+ mSignalStrength);
        return ((2 * mSignalStrength) - 113);
    }
}
