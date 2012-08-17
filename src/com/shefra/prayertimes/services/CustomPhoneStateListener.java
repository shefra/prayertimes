package com.shefra.prayertimes.services;

import com.shefra.prayertimes.manager.Manager;

import android.telephony.PhoneStateListener;

import android.content.Context;
import android.telephony.TelephonyManager;

public class CustomPhoneStateListener extends PhoneStateListener {

    //private static final String TAG = "PhoneStateChanged";
    Context context; //Context to make Toast if required 
    public CustomPhoneStateListener(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        switch (state) {
        case TelephonyManager.CALL_STATE_IDLE:
            //when Idle i.e no call
        	Manager.isPhoneIdle = true;
            break;
        case TelephonyManager.CALL_STATE_OFFHOOK:
            //when Off hook i.e in call
        	Manager.isPhoneIdle = false;
            break;
        case TelephonyManager.CALL_STATE_RINGING:
            //when Ringing
        	Manager.isPhoneIdle = false;
            break;
        default:
            break;
        }
    }
}