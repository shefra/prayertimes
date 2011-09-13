/*********************************************************
 * 15/10/1432H  13/11/2011 
 * This class tell the alarm manager when it's time to wake ServiceNot class for notification 
 * or converting to silent or general .
 * it calculate the specific time and Scheduled in the alarm manager Schedule   .
 * it read from xml to now  what is the next action (notification , silent , general )
 * then it will set the right time in the alarm manager . 
 */
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
				editor.putString("moode","notfication"); //reset the moode value 
				editor.commit();
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
			pref = PreferenceManager.getDefaultSharedPreferences(this);
			if(pref.getString("moode", "notfication").equals("notfication"))
				alarmManager.cancel(pendingIntent);//cancel scheduling to make sure there is no duplicated schedule  
			if(pref.getString("isCityChanged", "false").equals("true"))// If city changed we should reset moode value
			{
				editor = pref.edit();
				editor.putString("moode","notfication"); //reseting moode value
				editor.putString("isCityChanged", "false");
				editor.commit();
			}
				this.setAlarm();
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
	//this method will read from the xml to see what is the next action 
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
//calculate the remaining time for the next prayer and schedule it in the alarm manager   
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
		editor.putString("moode", "notfication");//set the moode value to notification ,, for the ServiceNot class
		editor.commit();
                 Calendar calendar = Calendar.getInstance();
                 calendar.setTimeInMillis(System.currentTimeMillis());
                 int nextPrayer = manager.nearestPrayerTime(h, m,s, yy, mm, dd);
     			int def =  manager.diffrent((h*3600+m*60+s),nextPrayer);             
                 calendar.add(Calendar.SECOND, def);
                 alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
		
	}
	//calculate the remaining time for silent and schedule it in the alarm manager   
	private void silent(){
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("FirstSetGeneralSilent", true)){//this if Condition to make sure that we don't schedule silent twice       
			Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(System.currentTimeMillis());
	        int sec = Integer.parseInt(pref.getString("silentStart", "0"));
	        if(sec == 0)
	        {
	        	String azanMode = pref.getString("notSound", "full");
	        	/***********
	        	if user chose full azan and Immediately silent ,, the silent will be after 4 minute (after azan finished ) 
	        	if he chose notification it will be after 1 minute . 
	        	*****/
	        	if(azanMode.equals("short")){
	        		sec = 1 ;
	        	}
	        	else
	        		sec = 4 ;
	        }
	        calendar.add(Calendar.SECOND, sec*60);
	        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
	        editor = pref.edit();
	        editor.putBoolean("FirstSetGeneralSilent", false);//Now we can't schedule silent until the value of FirstSetGeneralSilent be true
	        editor.commit();
		}
	}
	//calculate the remaining time for general and schedule it in the alarm manager   
	private void general(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("FirstSetGeneralSilent", true)){//this if Condition to make sure that we don't schedule general twice  		
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(System.currentTimeMillis());
			 int sec = Integer.parseInt(pref.getString("silentDuration", "20"));
	        calendar.add(Calendar.SECOND, sec*60);
	        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
	        editor = pref.edit();
	        editor.putBoolean("FirstSetGeneralSilent", false);//Now we can't schedule general until the value of FirstSetGeneralSilent be true  
	        editor.commit();
		}

	}


}
