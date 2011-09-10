package com.shefra.prayertimes.services;

import com.shefra.prayertimes.R;
import com.shefra.prayertimes.settings.AlertActivity;

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
	this.activMode();
}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		//this.activMode();
	}
	public void activMode(){
	       
	       
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String key = pref.getString("moode","notfication");
        Editor edit = pref.edit();
        if(key.equals("notfication"))
           
            this.notification();
        else if(key.equals("silent")){
                if(audioManagerState().equalsIgnoreCase("silent"))
                    edit.putString("nextState", "silent");
                else
                    edit.putString("nextState", "general");
                edit.commit();
                this.toSilent();
            }
        else if(key.equals("general")){
                String nextState = pref.getString("nextState", "general");
                if(nextState.equalsIgnoreCase("general"))
                    this.toGunral();
                else{
                    edit.putString("moode","notfication");
                    edit.commit();
                }
            }
    }
    public String audioManagerState(){
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                return "silent";
            case AudioManager.RINGER_MODE_VIBRATE:
                return "silent";
            case AudioManager.RINGER_MODE_NORMAL:
                return "general";
        }
        return "";
    }
	
	public void notification(){
		Intent intent;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		//int icon = R.drawable.notification_icon;
		String azanMode = pref.getString("notSound", "full") ;
		if(azanMode.equals("full")){
			 intent = new Intent(getBaseContext(), AlertActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(intent);
		}
		else if(azanMode.equals("short")){
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			CharSequence tickerText = "pray";
			long when = System.currentTimeMillis();
			Notification notification = new Notification(com.shefra.prayertimes.R.drawable.icon, tickerText, when);
			Context context = getApplicationContext();
			CharSequence contentTitle = getString(R.string.notTitle);
			CharSequence contentText = getString(R.string.notContent);
			Intent notificationIntent = new Intent(this, ServiceNot.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			notification.sound = Uri.parse("android.resource://com.shefra.prayertimes/raw/notification");
			//notification.defaults = Notification.DEFAULT_SOUND;
			notification.flags |= notification.FLAG_AUTO_CANCEL ;
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			mNotificationManager.notify(1, notification);
		}
		
		Editor editor = pref.edit();
		if(pref.getBoolean("disable", false))
			editor.putString("moode","notfication");
		else
			editor.putString("moode","silent");
		editor.commit();
		intent = new Intent(this, ServiceSetAlarm.class);
        startService(intent);
        this.stopSelf();
	}
	public void toSilent(){
		AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();
		editor.putString("moode","general"); 
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
		editor.putString("moode","notfication"); 
		editor.commit();
		Intent intent = new Intent(this, ServiceSetAlarm.class);
        startService(intent);
        this.stopSelf();
		
	}


}
