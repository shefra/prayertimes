package com.shefra.prayertimes;


import helper.DatabaseHelper;
import helper.TimeHelper;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.shefra.prayertimes.services.*;
import com.shefra.prayertimes.settings.About;
import com.shefra.prayertimes.settings.AutoCityMainActivity;
import com.shefra.prayertimes.settings.CityFinder;
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
 

// MainActivity represents main screen that is displayed to the user
// it Contains main data such as prayer times , remaining time until next prayer,
// city name , and so on ..
public class MainActivity extends Activity {
	private  ProgressDialog dialog;
	private  LocationManager locManager;
	
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		try{
		// create Manager object and use it to :
		// - load database into system folder at the first time
		// - get some data from database (prayer times .. )
		// - write some data to the database when necessary .
		Manager m = new Manager(getApplicationContext());
		DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
		
		try {
			// this method will work just one time as it is implemented
			// it copies the database file from assets folder to data folder
			// this step is necessary since that way Android system works :)
			databaseHelper.createDatabase();
		} catch (IOException e) {
			// TODO we have to log the error 
			e.printStackTrace();
		}
		
		// initialize the view objects with the data
		this.init();
		
		// run the app service , read ServiceSetAlarm for more info 
		Intent intent = new Intent(this, PrayerService.class);
		startService(intent);
		
		// check xml preference file to check if this is the first run for the app
		// in the user device.
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstStart = pref.getBoolean("firstStart",true);
        if(firstStart)
        {
        	// run some stuff at first time
        	// e.g. search for the use city 
        	// TODO : in the future we should run a wizard 
        	// it improve the usability for our app .
        	this.onFirstStart();
        	
        	// ok , change the flag to false , by this way we prevent onFirstStart method from running again
        	Editor edit = pref.edit();
        	edit.putBoolean("firstStart", false);
        	edit.commit();
        }
		}catch(Exception e){
			
		} 
		
		
	}

	public void init() {
		
		final Manager manager = new Manager(getApplicationContext());
		final DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
		
		// read city name using manager helper methods
		String cityName = databaseHelper.getCurrentCity().cityName;
		TextView cityTextView = (TextView) findViewById(R.id.cityName);
		cityTextView.setText(cityName);
		
		 // get current date
		// read Android/Java documentation for more info
		 Date date = new Date();
		 final int dd = date.getDate();//calendar.get(Calendar.DAY_OF_MONTH);
		 final int mm = date.getMonth()+1;//7;//calendar.get(Calendar.MONTH+1);
		 final int yy = date.getYear()+1900;//calendar.get(Calendar.YEAR);
	

		try {
			
			// get prayertimes as a List
			// index 0 : Fajr time
			// index 1 : Dhur time 
			// and so on , until index 4 witch is Isha time
			List<String> prayersList = Manager.getPrayerTimes(getApplicationContext(),dd, mm, yy);
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
		
			// Timer used to decrease the remaining time to next prayer
			updateRemainingTime(yy, mm, dd); //to calculate the nearest  pray 
			Timer myTimer =new Timer();
			TimerTask scanTask ;
			final Handler handler = new Handler();

			scanTask = new TimerTask() {
			    public void run() {
			            
						handler.post(new Runnable() {
			                    public void run() {
			                    		try {
			                    			updateRemainingTime(yy, mm, dd);
			                    		} catch (IOException e) {
			    							// TODO Auto-generated catch block
			    							
			    						} 
			                        }
			               });
			        }};

			
			// start the timer
			// 60000 ms == 60 seconds == 1 minutes :)
			myTimer.schedule(scanTask, 0, 60000); 
			
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public void updateRemainingTime(int yy, int mm, int dd ) throws IOException
	{
		Date date = new Date();
		 
		 int h = date.getHours();//calendar.get(Calendar.HOUR_OF_DAY);
		 int m = date.getMinutes();//calendar.get(Calendar.MINUTE);
		 int s = date.getSeconds();//calendar.get(Calendar.SECOND);
		// get remaining text view and change its value to " remaining time)
		 TextView remainingTime = (TextView) findViewById(R.id.remainingTime);
		// nearest prayer time ,
		// for example :Asr : 3:10
		// difference : Current time - Asr time == Current Time - 3:10 = remaining time
		int time = Manager.computeNearestPrayerTime(getApplicationContext(),h, m,s, yy, mm, dd);
		int def =  TimeHelper.diffrent((h*3600+m*60+s),time);
		remainingTime.setText(TimeHelper.secondsToTime(def));	
	}

	// add main menu items
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, getString(R.string.settings));
		menu.add(0, 3, 3, getString(R.string.about));
		// find the current city automatically
		menu.add(0, 4, 4, getString(R.string.autoCityTitle));
		menu.add(0, 5, 5, "New City Finder");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			// run Settings screen
			Intent myIntent = new Intent(this, SettingsActivity.class);
			startActivity(myIntent);
			return true;
		case 3:
			// run About screen 
			Intent MyIntent = new Intent (this , About.class);
			startActivity(MyIntent);
			
			return true;
		case 4:
			// run auto city finder dialog .
			new AutoCityMainActivity(this, dialog).startSearch();
			return true;
		case 5:
			// run About screen 
			Intent MyIntent2 = new Intent (this , CityFinder.class);
			startActivity(MyIntent2);
			
			return true;
		}
		return super.onOptionsItemSelected(item);

	}
	
	// update the view on resume
	public void onResume(){
		super.onResume();
		this.init();
	}
	
	
	// this method is triggered at the first time
	// put here what you want to execute at the first run for the app on this device
	
	public void onFirstStart(){
		// Show Dialog box that tell the user about GPS status and network provider status
		
		// this Service used to check if the GPS/Network providers work or not .
		locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
	    alertDialog.setTitle(getString(R.string.autoSearch));  
	    
	    // if the GPS/Network providers not work , tell the user
	    // then move it to the system settings screen
	    // by this way the user can change GPS/network status and come back again
	    if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){  
	    	alertDialog.setMessage(getString(R.string.autoSearchHowDisabled));
		    alertDialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which) {      	 
			    	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			    	// 111 is the code that we will use it with the method this.onActivityResult
			    	// it is used to get know that the user is comeback from
			    	// System settings screen to out app
			    	// check Android documentation for mire info
			    	startActivityForResult(intent, 111);
			    	 
			    }});
	    }
	    // if GPS or Network provider works that fine we don't need to send the user to system settings just tell the user that we are going to search for the
	    // current city
	    else
	    {
	    	alertDialog.setMessage(getString(R.string.autoSearchHowEnabled));	
	    	alertDialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which) { 
			    	// Run city finder method
			    	new AutoCityMainActivity(MainActivity.this, MainActivity.this.dialog).startSearch();
			    }});
	    }

	    // show the dialog 
	    alertDialog.show();
	}
	
	// this method is triggered when we send the user outside the app (e.g. to System Settings ) then the user come back again
	// to our app , we know that by resultCode
	// read Android App for more info about this method
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 111 && resultCode == 0){
        	
            LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);  
            
            // check GPS/Network status
            // if one of them enabled then start city finder method
            if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){  
            	new AutoCityMainActivity(MainActivity.this, MainActivity.this.dialog).startSearch();            	
            }else{
            	// if no then tell the user that we cannot search for the city while 
            	// the GPS or network provider does not work
        		AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
        	    alertDialog.setTitle("");  
        	    alertDialog.setMessage(getString(R.string.gpsAndNetworkIsDisabled));  
        	    alertDialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener(){
        	    public void onClick(DialogInterface dialog, int which) {  
        	    	return;          
        	    }});
        	    alertDialog.show();
            }
        }
    }

}