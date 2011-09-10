package com.shefra.prayertimes;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.shefra.prayertimes.services.*;
import com.shefra.prayertimes.settings.About;
import com.shefra.prayertimes.settings.AutoCityMainActivity;
import com.shefra.prayertimes.settings.SettingsActivity;
import com.shefra.prayertimes.manager.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends Activity {
	private  ProgressDialog dialog;
	private  LocationManager locManager;
	
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		Manager m = new Manager(getApplicationContext());
		try {
			m.createDatabase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// run service
		this.init();
		Intent intent = new Intent(this, ServiceSetAlarm.class);
		startService(intent);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstStart = pref.getBoolean("firstStart",true);
        if(firstStart)
        {
        	this.onFirstStart();
        	Editor edit = pref.edit();
        	edit.putBoolean("firstStart", false);
        	edit.commit();
        }
       
		
		
	}

	public void init() {
		
		final Manager manager = new Manager(getApplicationContext());
		
		String cityName = manager.getCurrentCity().cityName;
		TextView cityTextView = (TextView) findViewById(R.id.cityName);
		cityTextView.setText(cityName);
		 Date date = new Date();
		 final int dd = date.getDate();//calendar.get(Calendar.DAY_OF_MONTH);
		 final int mm = date.getMonth()+1;//7;//calendar.get(Calendar.MONTH+1);
		 final int yy = date.getYear()+1900;//calendar.get(Calendar.YEAR);
	

		try {
				
			List<String> prayersList = manager.getPrayerTimes(dd, mm, yy);
			TextView fajrTime = (TextView) findViewById(R.id.fajrTime);
			TextView duhrTime = (TextView) findViewById(R.id.duhrTime);
			TextView asrTime = (TextView) findViewById(R.id.asrTime);
			TextView magribTime = (TextView) findViewById(R.id.magribTime);
			TextView ishaTime = (TextView) findViewById(R.id.ishaTime);
			fajrTime.setText(prayersList.get(0));
			duhrTime.setText(prayersList.get(1));
			asrTime.setText(prayersList.get(2));
			magribTime.setText(prayersList.get(3));
			ishaTime.setText(prayersList.get(4));
		
			TimerMethod(manager,yy, mm, dd); //to calculate the nearest  pray 
			Timer myTimer =new Timer();
			TimerTask scanTask ;
			final Handler handler = new Handler();

			scanTask = new TimerTask() {
			    public void run() {
			            
						handler.post(new Runnable() {
			                    public void run() {
			                    		try {
											TimerMethod(manager,yy, mm, dd);
			                    		} catch (IOException e) {
			    							// TODO Auto-generated catch block
			    							e.printStackTrace();
			    						} 
			                        }
			               });
			        }};

			

//									  start ,rebated 	
			myTimer.schedule(scanTask, 0, 60000); 
			
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	public void TimerMethod(Manager manager ,int yy, int mm, int dd ) throws IOException
	{
		Date date = new Date();
		 
		 int h = date.getHours();//calendar.get(Calendar.HOUR_OF_DAY);
		 int m = date.getMinutes();//calendar.get(Calendar.MINUTE);
		 int s = date.getSeconds();//calendar.get(Calendar.SECOND);
		TextView remainingTime = (TextView) findViewById(R.id.remainingTime);
		int time = manager.nearestPrayerTime(h, m,s, yy, mm, dd);
		int def =  manager.diffrent((h*3600+m*60+s),time);
		remainingTime.setText(Manager.secondsToTime(def));	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, getString(R.string.settings));
		menu.add(0, 3, 3, getString(R.string.about));
		menu.add(0, 4, 4, getString(R.string.autoCityTitle));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			Intent myIntent = new Intent(this, SettingsActivity.class);
			startActivity(myIntent);
			return true;
		case 3:
//			
//			AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
//		    alertDialog.setTitle(getString(R.string.about));  
//		    alertDialog.setMessage(getString(R.string.teamName));  
//		    alertDialog.setButton(getString(R.string.close), new DialogInterface.OnClickListener(){
//		    public void onClick(DialogInterface dialog, int which) {  
//		        return;  
//		    }});
//		    alertDialog.show();
			Intent MyIntent = new Intent (this , About.class);
			startActivity(MyIntent);
			
			return true;
		case 4:
			
			new AutoCityMainActivity(this, dialog).startSearch();
		}
		return super.onOptionsItemSelected(item);

	}
	
	public void onResume(){
		super.onResume();
		this.init();
	}
	
	
	//TODO : support NETWORK_PROVIDER  
	public void onFirstStart(){
		locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
	    alertDialog.setTitle(getString(R.string.autoSearch));  
	    if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){  
	    	alertDialog.setMessage(getString(R.string.autoSearchHowDisabled));
		    alertDialog.setButton(getString(R.string.close), new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which) {      	 
			    	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			    	startActivityForResult(intent, 111);
			    	 
			    }});
	    }
	    else
	    {
//	    	alertDialog.setMessage(getString(R.string.autoSearchHowEnabled));	
	    	alertDialog.setButton(getString(R.string.close), new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which) {  
			    	new AutoCityMainActivity(MainActivity.this, MainActivity.this.dialog).startSearch();
			    }});
	    }

	    alertDialog.show();
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 111 && resultCode == 0){
            LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);  
            
            if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){  
            	new AutoCityMainActivity(MainActivity.this, MainActivity.this.dialog).startSearch();            	
            }else{
        		AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
        	    alertDialog.setTitle("");  
        	    alertDialog.setMessage(getString(R.string.gpsAndNetworkIsDisabled));  
        	    alertDialog.setButton(getString(R.string.close), new DialogInterface.OnClickListener(){
        	    public void onClick(DialogInterface dialog, int which) {  
        	    	return;          
        	    }});
        	    alertDialog.show();
            }
        }
    }

}