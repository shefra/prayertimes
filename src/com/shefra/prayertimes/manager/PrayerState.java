package com.shefra.prayertimes.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
 
public class PrayerState {
	public static final int UNKNOWN = 1000;
	public static final int WAITING_AZAN = 0;
	public static final int PRE_DOING_AZAN = 1;
	public static final int DOING_AZAN = 2;
	public static final int WAITING_PRAYER = 3;

	private Context context;

	public PrayerState(Context context) {
		this.context = context;
		setNextState(WAITING_AZAN);
	}

	public int getCurrentState() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this.context);
		return pref.getInt("prayerState", WAITING_AZAN);
	}

	public void setNextState(int state) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this.context);
		Editor editor = pref.edit();
		editor.putInt("prayerState", state);
		editor.putLong("stateChangeTime", System.currentTimeMillis());
		editor.commit();
	}

}
