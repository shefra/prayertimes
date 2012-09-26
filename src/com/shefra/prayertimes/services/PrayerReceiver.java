package com.shefra.prayertimes.services;


import java.io.IOException;
import java.util.Date;

import com.shefra.prayertimes.R;
import com.shefra.prayertimes.helper.TimeHelper;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.PrayerState;
import com.shefra.prayertimes.manager.Preference;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

/* 
 * The Prayer Alarm Life Cycle has three states :
 * 1- Waiting for the Next Azan: Wait until the Azan time.
 * 2- PreDoing the Azan: if the mobile mode is in neither silent nor vibrate
 * 	  then play the Azan soudn track + move to the next state when the Azan sound track finished.Currently we don;t know when, but we assume that three minutes is enought.
 * 3- On Doing the Azan: for the future. when we able to detect that the sound track is finished then move to the next state. currently , we move to the next state after 3 minutes always.
 * 4- On Waiting the prayer:  
 */
public class PrayerReceiver extends BroadcastReceiver {

	private PrayerState prayerState;
	private AudioManager am;
	private Context context;
	private SharedPreferences pref;
	private Editor editor;
	private int silentDuration;
	private int silentStart ;
	private int delayMilliSeconds = 1000 * 60;  // one minute by default.
	private Object obj;


	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		pref = PreferenceManager.getDefaultSharedPreferences(this.context);
		
		Manager m = new Manager(context);
		Preference p = m.getPreference();
		this.silentDuration = p.getSilentDuration();
		this.silentStart = p.getSilentStart();
		//Log.i("com.shefrah.prayertimes","SilentDuration:"+ this.silentDuration);
		editor = pref.edit();
		try {
			
			prayerState = Manager.getPrayerState();
			am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			switch (prayerState.getCurrentState()) {

			case PrayerState.WAITING_AZAN:
				onWaitingAzan(); 

				break;
			case PrayerState.DOING_AZAN:
				onDoingAzan();
				break;

			case PrayerState.WAITING_PRAYER:
				onWaitingPrayer();

				break;

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//Log.e("com.shefrah.prayertimes", e.getMessage());
		}
		
	}

	
	public int getDelayMilliSeconds() {
		return delayMilliSeconds;
	}

	public void setDelayMilliSeconds(int delayMilliSeconds) {
		this.delayMilliSeconds = delayMilliSeconds;
	}

	private void onWaitingAzan() {
		try {
			//Log.i("com.shefrah.prayertimes",
			//"WAITING_AZAN:" + Long.toString(System.currentTimeMillis()));

			// change it to normal mode only if you are not waiting the prayer (
			// out of the prayer time)
			// don't change it to Normal until you have already have changed it
			// to silent
			boolean isRingerModeChangedToSilent = pref.getBoolean(
					"isRingerModeChangedToSilent", false);
			if (isRingerModeChangedToSilent == true) {
				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				editor.putBoolean("isRingerModeChangedToSilent", false);
				editor.commit();
			}

			// What is the remaining time until the next prayer ?
			Date date = new Date();
			int dd = date.getDate();
			int mm = date.getMonth() + 1;
			int yy = date.getYear() + 1900;
			int h = date.getHours();
			int m = date.getMinutes();
			int s = date.getSeconds();
			int nearestPrayerTime = Manager.computeNearestPrayerTime(context,
					h, m, s, yy, mm, dd);
			int deffTime = TimeHelper.different((h * 3600 + m * 60 + s),
					nearestPrayerTime);
			deffTime = deffTime * 1000; // to milliseconds

			// ok , come back after X seconds to do the Azan
			prayerState.setNextState(PrayerState.DOING_AZAN);
			this.delayMilliSeconds = deffTime;
			//Log.i("com.shefrah.prayertimes","AZAN_AFTER:" + deffTime);
			//this.postDelayed((Runnable)obj, delayMilliSeconds );
			Manager.updatePrayerAlarm(delayMilliSeconds);

		} catch (Exception e) {
			//Log.e("com.shefrah.prayertimes", e.getMessage());
		}
	}

	private void onDoingAzan() {
		//Log.i("com.shefrah.prayertimes",
		//		"DOING_AZAN:" + Long.toString(System.currentTimeMillis()));
		

		prayerState.setNextState(PrayerState.WAITING_PRAYER);
		this.delayMilliSeconds = this.silentStart;
		if(this.delayMilliSeconds < 2000*60)
			this.delayMilliSeconds =2000*60; // two minutes  - at lease  
		Manager.playAzanNotification(context);
		//this.postDelayed((Runnable)obj, delayMilliSeconds );
		Manager.updatePrayerAlarm(delayMilliSeconds);


	}

	private void onWaitingPrayer() {
		//Log.i("com.shefrah.prayertimes","WAITING_PRAYER:" + Long.toString(System.currentTimeMillis()));
		Manager manager = new Manager(this.context);
		Preference preference = manager.getPreference();
		AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		if(am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL && preference.isAutoSilentDisabled()==false ){
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			editor.putBoolean("isRingerModeChangedToSilent", true);
			editor.commit();
		}
		this.delayMilliSeconds = silentDuration;
		prayerState.setNextState(PrayerState.WAITING_AZAN);
		//this.postDelayed((Runnable)obj, delayMilliSeconds );
		Manager.updatePrayerAlarm(delayMilliSeconds);


	}


}