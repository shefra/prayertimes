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
		public void onCreate(){
		
			try {
				this.setAlarm();
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
			this.setAlarm();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getSec(int hh,int mm,int ss){
		return ((hh*3600)+(mm*60)+ss);
	}   
	public void setAlarm() throws IOException{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String key = pref.getString("moode","notfication");
		Editor editor = pref.edit();
		//notfication();
		if(key.equals("notfication"))
		{
			notfication();
		}
		else if (key.equals("silent")){
			silent();
		}
		else if (key.equals("general")){
			general();
		}
	}
	private void notfication() throws IOException{
		Manager m = new Manager(getApplicationContext());
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();
		int current,nextPrayer;
		editor.putString("moode","notfication"); 
		editor.commit();
		 Intent myIntent = new Intent(ServiceSetAlarm.this, ServiceNot.class);
		PendingIntent pendingIntent = PendingIntent.getService(ServiceSetAlarm.this, 0, myIntent, 0);

                 AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                 Calendar calendar = Calendar.getInstance();
                 calendar.setTimeInMillis(System.currentTimeMillis());
                 Date date = new Date();
                 current = this.getSec(date.getHours(),date.getMinutes(), date.getSeconds());
                 
                 nextPrayer=m.nearestPrayerTime(date.getHours(),date.getMinutes(), date.getSeconds()
                		 , date.getYear()+1900, date.getMonth()+1, date.getDay());
                 calendar.add(Calendar.SECOND, m.diffrent(current,nextPrayer));
                 alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
		
	}
	private void silent(){
		Intent myIntent = new Intent(ServiceSetAlarm.this, ServiceNot.class);
		PendingIntent pendingIntent = PendingIntent.getService(ServiceSetAlarm.this, 0, myIntent, 0);

                 AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                 Calendar calendar = Calendar.getInstance();
                 calendar.setTimeInMillis(System.currentTimeMillis());
                 SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
         		 int sec = pref.getInt("silentStart", 20);
                 calendar.add(Calendar.SECOND, sec*60);
                 alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
		
	}
	
	private void general(){
		Intent myIntent = new Intent(ServiceSetAlarm.this, ServiceNot.class);
		PendingIntent pendingIntent = PendingIntent.getService(ServiceSetAlarm.this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		 int sec = pref.getInt("silentDuration", 20);
        calendar.add(Calendar.SECOND, sec*60);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        this.stopSelf();
	}
}
