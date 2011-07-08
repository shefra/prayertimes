package com.shefra.prayertimes.services;

import java.util.Calendar;

import android.R;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
public class ServiceNot extends Service {
public void onCreate(){
	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
	String key = pref.getString("moode","notfication");
	if(key.equals("notfication"))
		this.notification();
	else if(key.equals("silent"))
		this.toSilent();
	else if(key.equals("Gunral"))
		this.toGunral();
}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	public void test(){
		
	}
	public int getSec(String time){
		int sec = 0;
		String []temp = time.split(":");
		sec = Integer.parseInt(temp[0]) * 3600;
		sec += Integer.parseInt(temp[1]) * 60;
		temp = temp[2].split(" ");
		sec += Integer.parseInt(temp[0]);
		return sec;
	}
	public void setAlarm(){
		//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        //Editor editor = pref.edit();
        //editor.putString("moode","notfication"); 
        //editor.commit();
		Intent myIntent = new Intent(ServiceNot.this, ServiceNot.class);
		PendingIntent pendingIntent = PendingIntent.getService(ServiceNot.this, 0, myIntent, 0);

                 AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                 Calendar calendar = Calendar.getInstance();
                 calendar.setTimeInMillis(System.currentTimeMillis());
                 calendar.add(Calendar.SECOND, 10);
                 alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
                 
		
	}
	public void notification(){
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		CharSequence tickerText = "حان وقت الصلاة";
		long when = System.currentTimeMillis();
		//int icon = R.drawable.notification_icon;
		Notification notification = new Notification(com.shefra.prayertimes.R.drawable.icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = "الصلاة";
		CharSequence contentText = "Hello Worlsilentd!";
		Intent notificationIntent = new Intent(this, ServiceNot.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		//notification.sound |= notification.sound.D;
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		mNotificationManager.notify(1, notification);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();
		editor.putString("moode","silent"); 
		editor.commit();
		Intent intent = new Intent(this, ServiceSetAlarm.class);
        startService(intent);
        this.stopSelf();
	}
	public void toSilent(){
		AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();
		editor.putString("moode","Gunral"); 
		editor.commit();
		Intent intent = new Intent(this, ServiceSetAlarm.class);
        startService(intent);
        this.stopSelf();
		
	}
	public void toGunral(){
		AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();
		editor.putString("moode","setNot"); 
		editor.commit();
		
	}


}
