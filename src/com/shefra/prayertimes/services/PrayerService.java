package com.shefra.prayertimes.services;

import com.shefra.prayertimes.manager.Manager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class PrayerService extends Service {

	private static IntentFilter s_intentFilter;
	long mStartTime;
	
	static {
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		try {
			super.onCreate();
			Manager.initPrayerState(this);
			Manager.initPrayerAlarm(this, PrayerReceiver.class);
			this.registerReceiver(this.m_timeChangedReceiver, s_intentFilter);
			//prayerHandler = new PrayerHandler2(this);
			//prayerHandler.postDelayed(mUpdateTimeTask, 10000); // as soon as possible ( 10 seconds )

		} catch (Exception e) {
			//Log.e("onCreateService", e.getMessage(), e.getCause());
		}

	}

	 // time changed listener
	 private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            final String action = intent.getAction();

	            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
	                action.equals(Intent.ACTION_TIMEZONE_CHANGED))
	            {
	            	Manager.cancelPrayerAlarm();
	            	Manager.initPrayerState(PrayerService.this);
	    			Manager.initPrayerAlarm(PrayerService.this,PrayerReceiver.class);
	            	
	            }
	        }
	    };
	
	
	


}
