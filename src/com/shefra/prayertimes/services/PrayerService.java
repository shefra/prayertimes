package com.shefra.prayertimes.services;

import com.shefra.prayertimes.manager.Manager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class PrayerService extends Service {

	long mStartTime;
	private PrayerHandler prayerHandler;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		try {
			super.onCreate();
			Manager.initPrayerStateMachine(this);
			//Manager.initPrayerAlarm(this, PrayerReceiver.class);
			prayerHandler = new PrayerHandler(this);
			mStartTime = SystemClock.uptimeMillis();
			prayerHandler.postDelayed(mUpdateTimeTask, 10000); // as soon as possible ( 10 seconds )

		} catch (Exception e) {
			Log.e("onCreateService", e.getMessage(), e.getCause());
		}

	}

	private Runnable mUpdateTimeTask = new Runnable() {
		private int seconds;

		public void run() {
			final long start = mStartTime;
			long millis = SystemClock.uptimeMillis() - start;
			seconds = (int) (millis / 1000);
			Message msg = new Message();
			msg.what = seconds;
			prayerHandler.sendMessage(msg);
			prayerHandler.postDelayed(this, prayerHandler.getDelayMilliSeconds());
		}
	};
	


}
