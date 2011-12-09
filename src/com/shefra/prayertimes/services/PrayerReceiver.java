package com.shefra.prayertimes.services;

import java.io.IOException;
import java.util.Date;

import com.shefra.prayertimes.R;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.PrayerStateMachine;
import com.shefra.prayertimes.settings.AlertActivity;

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

	private PrayerStateMachine prayerState;
	private AudioManager am;
	private Context context;
	private SharedPreferences pref;
	private Editor editor;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		 pref = PreferenceManager
		.getDefaultSharedPreferences(this.context);
		 editor = pref.edit();
		try {

			prayerState = Manager.getPrayerStateMachine();
			am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			switch (prayerState.getPrayerState()) {

			case PrayerStateMachine.WAITING_AZAN:
				onWaitingAzan();

				break;
			case PrayerStateMachine.PRE_DOING_AZAN:

				onPreDoingAzan();

				break;
			case PrayerStateMachine.DOING_AZAN:

				break;

			case PrayerStateMachine.WAITING_PRAYER:
				onWaitingPrayer();

				break;

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("OnReceive Error", e.getMessage());
		}
	}

	private void onWaitingAzan() {
		Log.i("WAITING_AZAN", Long.toString(System.currentTimeMillis()));
		
		// don't change to Normal until you have already changed it to silent
		boolean isRingerModeChangedToSilent = pref.getBoolean("isRingerModeChangedToSilent", false);
		if(isRingerModeChangedToSilent == true){
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			editor.putBoolean("isRingerModeChangedToSilent", false);
			editor.commit();
		}

		/*
		 * Date date = new Date(); int dd = date.getDate(); int mm =
		 * date.getMonth() + 1; int yy = date.getYear() + 1900; int h =
		 * date.getHours(); int m = date.getMinutes(); int s =
		 * date.getSeconds(); long nearestPrayerTime =
		 * Manager.computeNearestPrayerTime( context, h, m, s, yy, mm,
		 * dd);
		 */
		long nearestPrayerTime = 25;
		// less then 60 seconds
		if (nearestPrayerTime < 10) {
			prayerState.setPrayerState(PrayerStateMachine.PRE_DOING_AZAN);
			// check again after X milliseconds
			Manager.updatePrayerAlarm(nearestPrayerTime * 1000);

		} else if (nearestPrayerTime < 5) { // almost there
			onPreDoingAzan();
		} else {
			// check again after 1 minute milliseconds
			prayerState.setPrayerState(PrayerStateMachine.WAITING_AZAN);
			Manager.updatePrayerAlarm(1 * 1000);
			
		}
		
	}


	private void onPreDoingAzan() {
		Log.i("PRE_DOING_AZAN", Long.toString(System.currentTimeMillis()));

		long stateChangeTime = pref.getLong("stateChangeTime", 0);
		long deltaTime = System.currentTimeMillis() - stateChangeTime;

		// TODO : make it DOING_AZAN , and in DOING_AZAN block check to see if
		// the Azan finished
		// since I don't know how to know if the mp3 track is done? I will
		// assume it will finish
		// after 3 minutes always, so just move directly to WAITING_PRAYER
		
		// is the RingerMode in Silent ? if yes then we don't need to make it silent!even more, don't play the Azan sound! Logic ?
		// also , if the Azan time was gone , then just move to the next state. this might happens when the mobile was turned off.
		if(am.getRingerMode() == AudioManager.RINGER_MODE_SILENT || am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE || deltaTime > 30 * 1000)
		{
			prayerState.setPrayerState(PrayerStateMachine.WAITING_AZAN);
			// wait for the next prayer.
			Manager.updatePrayerAlarm( 1000);
		}
		else
		{
			prayerState.setPrayerState(PrayerStateMachine.WAITING_PRAYER);
			// check again after three minutes (I think the 3 minutes are enough to
			// complete the Azan)
			
			// run the Azan sound track
			Manager.playAzanNotification(context);

		}

	
	}
	
	private void onWaitingPrayer() {
		Log.i("WAITING_PRAYER", Long.toString(System.currentTimeMillis()));

		long stateChangeTime = pref.getLong("stateChangeTime", 0);
		long deltaTime = System.currentTimeMillis() - stateChangeTime;
		long silentDuration = 30*60*1000;
		
		boolean isRingerModeChangedToSilent = pref.getBoolean("isRingerModeChangedToSilent", false);
		if(isRingerModeChangedToSilent == false){
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			editor.putBoolean("isRingerModeChangedToSilent", true);
			editor.commit();
		}
		
		// if state == waiting prayer & the waiting time was > 30 minutes. just move to the next state
		// save last change time of the 
		
		
		
		// wait for the next prayer.
		if(deltaTime > silentDuration ){
			prayerState.setPrayerState(PrayerStateMachine.WAITING_AZAN);
			
		}
		
		Manager.updatePrayerAlarm(1000);

	}


}
