package com.shefra.prayertimes.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PrayerStateMachine {
	public static final int WAITING_AZAN    = 0;
	public static final int PRE_DOING_AZAN = 1;
	public static final int DOING_AZAN     = 2;
	public static final int WAITING_PRAYER   = 3;
	
	private Context context;
	
	public PrayerStateMachine(Context context){
		this.context = context;
		setPrayerState(WAITING_AZAN);
	}
	public int getPrayerState(){
		SharedPreferences pref = PreferenceManager
		.getDefaultSharedPreferences(this.context);
		return pref.getInt("prayerState", WAITING_AZAN);
	}
	
	public void setPrayerState(int state){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.context);
		Editor editor = pref.edit();
		editor.putInt("prayerState", state);
		editor.putLong("stateChangeTime", System.currentTimeMillis());
		editor.commit();
	}
	

}
