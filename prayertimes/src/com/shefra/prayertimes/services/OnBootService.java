package com.shefra.prayertimes.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

	public class OnBootService extends BroadcastReceiver {

		 public void onReceive(Context context, Intent intent) {
			 if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
				 SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(context);
			Editor editor = pref.edit();
			editor.putString("test", "true");
			editor.commit();
				 Intent serviceIntent = new Intent(context, ServiceSetAlarm.class);
			     context.startService(serviceIntent);
			 }else{
				 Log.e("OnBootService", "Received unexpected intent " + intent.toString());
			 }
		 }
	}