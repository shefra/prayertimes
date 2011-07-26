package com.shefra.prayertimes.services;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.shefra.prayertimes.manager.Manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;


public class ServiceSetAlarm extends Service{
	SharedPreferences pref ;
	Editor editor ;
	Intent myIntent;
	PendingIntent pendingIntent;
	AlarmManager alarmManager;
		public void onCreate(){
		
			try {
				 myIntent = new Intent(ServiceSetAlarm.this, ServiceNot.class);
				 pendingIntent = PendingIntent.getService(ServiceSetAlarm.this, 0, myIntent, 0);
		         alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
				pref = PreferenceManager.getDefaultSharedPreferences(this);
				editor = pref.edit();
				editor.putString("moode","notfication"); 
				editor.commit();
				//if(!pref.getBoolean("enabled", false))
					this.setAlarm();
				//else
					//this.stopSelf();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		try {
			alarmManager.cancel(pendingIntent);
			pref = PreferenceManager.getDefaultSharedPreferences(this);
			if(pref.getString("isCityChanged", "false").equals("true"))
			{
				editor = pref.edit();
				editor.putString("moode","notfication"); 
				editor.putString("isCityChanged", "false");
				editor.commit();
			}
			//if(!pref.getBoolean("enabled", false))
				this.setAlarm();
			//else
				//this.stopSelf();
			//this.setAlarm();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		//alarmManager.cancel(pendingIntent);
		
	}

	public int getSec(int hh, int mm, int ss) {
		return ((hh * 3600) + (mm * 60) + ss);
	}

	public void setAlarm() throws IOException {
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		String key = pref.getString("moode", "notfication");
		editor = pref.edit();
		if (key.equals("notfication")) {
			notfication();
		} else if (key.equals("silent")) {
			silent();
		} else if (key.equals("general")) {
			general();
		}
	}

	private void notfication() throws IOException {
		Date date = new Date();
		int dd = date.getDate();
		int mm = date.getMonth() + 1;
		int yy = date.getYear() + 1900;
		int h = date.getHours();
		int m = date.getMinutes();
		int s = date.getSeconds();
		Manager manager = new Manager(getApplicationContext());
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		editor = pref.edit();
		editor.putString("moode", "notfication");
		editor.commit();
                 Calendar calendar = Calendar.getInstance();
                 calendar.setTimeInMillis(System.currentTimeMillis());
                 int nextPrayer = manager.nearestPrayerTime(h, m,s, yy, mm, dd);
     			int def =  manager.diffrent((h*3600+m*60+s),nextPrayer);             
                 calendar.add(Calendar.SECOND, def);
                 alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
		
	}
	private void silent(){
		Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        int sec = Integer.parseInt(pref.getString("silentStart", "20"));
        calendar.add(Calendar.SECOND, sec*60);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
		
	}
	
	private void general(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        pref = PreferenceManager.getDefaultSharedPreferences(this);
		 int sec = Integer.parseInt(pref.getString("silentDuration", "20"));
        calendar.add(Calendar.SECOND, sec*60);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);

	}


}
