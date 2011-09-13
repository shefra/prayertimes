/*
 * OnBootService Class is a: Android Service that boots with the System to run 
 * ServiceSetAlarm so the set alarm won't be lost.
 */

package com.shefra.prayertimes.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

	public class OnBootService extends BroadcastReceiver {

		 public void onReceive(Context context, Intent intent) {
			 if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
				 Intent serviceIntent = new Intent(context, ServiceSetAlarm.class);
			     context.startService(serviceIntent);
			 }else{
				 Log.e("OnBootService", "Received unexpected intent " + intent.toString());
			 }
		 }
	}