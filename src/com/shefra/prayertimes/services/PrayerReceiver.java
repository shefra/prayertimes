package com.shefra.prayertimes.services;


import java.io.IOException;
import java.util.Date;

import com.shefra.prayertimes.R;
import com.shefra.prayertimes.helper.TimeHelper;
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
	private int silentDuration = 3 * 60 * 1000;
	// the time range is start from -30 to +30 seconds
	// so if the Azan time was on 12:10:20
	// and our app checked on 12:10:28 , then it should do Azan even if it's not
	// exactly the same time.
	// but if the app checked on 12:10:55 then it should not do Azan. since it's
	// out of azan time.
	private int azanTimeRange = 30 * 1000;

	private int intervalTime = 1000 * 10; // each x seconds check to see if the
											// Azan time is coming or not .

	private int soundTrackDuration = 40 * 1000; // Azan sound track duration
 
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		pref = PreferenceManager.getDefaultSharedPreferences(this.context);
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
			Log.e("com.shefrah.prayertimes", e.getMessage());
		}
	}

	private void onWaitingAzan() {
		try{
		Log.i("com.shefrah.prayertimes","WAITING_AZAN:" + Long.toString(System.currentTimeMillis()));
		System.out.print("com.shefrah.prayertimes" + " WAITING_AZAN:");
 
		// change it to normal mode only if you are not waiting the prayer ( out of the prayer time)
		// don't change it to Normal until you have already have changed it to silent
		boolean isRingerModeChangedToSilent = pref.getBoolean(
				"isRingerModeChangedToSilent", false);
		if (isRingerModeChangedToSilent == true) {
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			editor.putBoolean("isRingerModeChangedToSilent", false);
			editor.commit();
		}
		Date date = new Date(); 
		int dd = date.getDate();
		int mm = date.getMonth() + 1;
		int yy = date.getYear() + 1900;
		int h = date.getHours();
		int m = date.getMinutes();
		int s = date.getSeconds();
		int nearestPrayerTime = Manager.computeNearestPrayerTime(context, h,
				m, s, yy, mm, dd);
		int deffTime =  TimeHelper.diffrent((h*3600+m*60+s),nearestPrayerTime);
		deffTime = deffTime * 1000; // to millieseconds
		// Suppose AzanTimeRange = 30 seconds

		// case 1 : AzanTime = 12:10:12 , currentTime 12:10:16 Or currentTime
		// 12:10:06 => Do the Azan now since we in the range
		if (deffTime <= azanTimeRange
				&& deffTime >= -azanTimeRange) {// almost there
			prayerState.setPrayerState(PrayerStateMachine.PRE_DOING_AZAN);
			onPreDoingAzan();
		}
		// case 2 : AzanTime = 12:10:55 , currentTime 12:10:00 then recheck
		// again after 55 milliseconds
		else if (deffTime <= intervalTime) {
			prayerState.setPrayerState(PrayerStateMachine.PRE_DOING_AZAN);
			Manager.updatePrayerAlarm(intervalTime - deffTime);
		} else {

			// check again after x milliseconds
			prayerState.setPrayerState(PrayerStateMachine.WAITING_AZAN);
			Manager.updatePrayerAlarm(intervalTime);

		}
		}catch(Exception e){
			Log.e("com.shefrah.prayertimes", e.getMessage());
		}
	}

	private void onPreDoingAzan() {
		Log.i("com.shefrah.prayertimes","PRE_DOING_AZAN:" + Long.toString(System.currentTimeMillis()));

		// when the state was changed .
		long stateChangeTime = pref.getLong("stateChangeTime", 0);

		// deltatime : useful to know when the Azan happened.
		// based on that we can make a decision :
		// - Is it time of the Azan ?
		// - Is it time of waiting the prayer?
		// - Anything else ?
		// e.g. if deltatime > 30 seconds then don't do the Azan.But wait the
		// prayer
		// if deltatime > 30 minutes , don't wait the prayer , but wait the next
		// Azan.
		long deltaTime = System.currentTimeMillis() - stateChangeTime;

		// TODO : make it DOING_AZAN , and in DOING_AZAN block check to see if
		// the Azan finished
		// since I don't know how to know if the mp3 track is done? I will
		// assume it always finishes after 3 minutes , so just move directly to
		// WAITING_PRAYER

		// is the RingerMode in Silent ? if yes then we don't need to make it
		// silent!even more, don't play the Azan sound! Logic ?
		// also , if the prayer time was gone , then just move to the next state
		// ( no need to make it silent ). this might happens when the mobile was
		// turned off.
		if (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT
				|| am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE
				|| deltaTime >= silentDuration) {
			prayerState.setPrayerState(PrayerStateMachine.WAITING_AZAN);
			// wait for the next prayer.
			Manager.updatePrayerAlarm(intervalTime);
		} else {

			// check again after three minutes (I think 3 minutes are enough to
			// complete the Azan)
			if (deltaTime <= azanTimeRange) {
				prayerState.setPrayerState(PrayerStateMachine.WAITING_PRAYER);
				Manager.updatePrayerAlarm(soundTrackDuration);
				// run the Azan sound track
				Manager.playAzanNotification(context);
			} else {
				// we run out of time , so just wait for the prayer without
				// doing Azan
				Manager.updatePrayerAlarm(intervalTime);
				prayerState.setPrayerState(PrayerStateMachine.WAITING_PRAYER);
			}

		}

	}

	private void onWaitingPrayer() {
		Log.i("com.shefrah.prayertimes","WAITING_PRAYER:"+Long.toString(System.currentTimeMillis()));

		long stateChangeTime = pref.getLong("stateChangeTime", 0);
		long deltaTime = System.currentTimeMillis() - stateChangeTime;

		//no need to change the mobile mode
		// if the azan time - current time > silent duration ==> After the prayer
		if (deltaTime > silentDuration) {
			
			prayerState.setPrayerState(PrayerStateMachine.WAITING_AZAN);
			

		}else{
		// just change it to silent once. the user might not want to make it
		// silent.
		boolean isRingerModeChangedToSilent = pref.getBoolean(
				"isRingerModeChangedToSilent", false);
		if (isRingerModeChangedToSilent == false) {
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			editor.putBoolean("isRingerModeChangedToSilent", true);
			editor.commit();
		}
		}


		Manager.updatePrayerAlarm(intervalTime);

	}

}
