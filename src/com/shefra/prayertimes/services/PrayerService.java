package com.shefra.prayertimes.services;

import com.shefra.prayertimes.manager.Manager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PrayerService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		try{
		super.onCreate();
		Manager.initPrayerStateMachine(this);
		Manager.initPrayerAlarm(this, PrayerReceiver.class);
		}catch (Exception e) {
			Log.e("onCreateService",e.getMessage(),e.getCause());
		}
		
	}
}
