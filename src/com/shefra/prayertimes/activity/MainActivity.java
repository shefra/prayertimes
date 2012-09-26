package com.shefra.prayertimes.activity;



import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.shefra.prayertimes.R;
import com.shefra.prayertimes.helper.DatabaseHelper;
import com.shefra.prayertimes.helper.TimeHelper;
import com.shefra.prayertimes.helper.Typefaces;
import com.shefra.prayertimes.manager.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
 

// MainActivity represents main screen that is displayed to the user
// it Contains main data such as prayer times , remaining time until next prayer,
// city name , and so on ..
public class MainActivity extends Activity {
	

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
			databaseHelper.close();
		} catch (IOException e) {
			//Log.e("tomaanina",e.getCause() + ":" + e.getMessage());
		}
		
		// initialize the view objects with the data
		this.init();
		
		// run the app service , read PrayerService for more info 
		m.restartPrayerService(this);
		
		// check xml preference file to check if this is the first run for the app
		// in the user device.
		
		Preference pref = m.getPreference();
        if(pref.isFirstStart())
        {
        	// run some stuff at first time
        	// e.g. search for the use city 
        	// TODO : in the future we should run a wizard 
        	// it improve the usability for our app .
        	this.onFirstStart();
        	
        	// ok , change the flag to false , by this way we prevent onFirstStart method from running again
        	pref.setFirstStart(false);
        }
		}catch(Exception e){
			
		} 
		
		
	}

	public void init() {
		
		final Manager manager = new Manager(getApplicationContext());
		Preference preference = manager.getPreference();

		preference.fetchCurrentPreferences();
		TextView cityTextView = (TextView) findViewById(R.id.cityName);
		cityTextView.setText(preference.city.name);
		cityTextView.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/DroidNaskh-Regular.ttf"));
		
		TextView remainingTV = (TextView) findViewById(R.id.remainingText);
		TextView cityLabelTV = (TextView) findViewById(R.id.cityLabel);
		TextView fajrTV = (TextView) findViewById(R.id.fajrTV);
		TextView duhrTV = (TextView) findViewById(R.id.duhrTV);
		TextView asrTV = (TextView) findViewById(R.id.asrTV);
		TextView magribTV = (TextView) findViewById(R.id.magribTV);
		TextView ishaTV = (TextView) findViewById(R.id.ishaTV);

		remainingTV.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/DroidNaskh-Regular.ttf"));
		cityLabelTV.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/DroidNaskh-Regular.ttf"));
		
		fajrTV.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/KacstTitle.ttf"));
		duhrTV.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/KacstTitle.ttf"));
		asrTV.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/KacstTitle.ttf"));
		magribTV.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/KacstTitle.ttf"));
		ishaTV.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/KacstTitle.ttf"));

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
			
			// Dump sol. replace AM and PM with Arabic Letters, The current font is not good enough to be used
			// with Arabic Letters
			//String timeFormatted = prayersList.get(0).replace("AM", this.getString(R.string.am)).replace("PM",this.getString(R.string.pm));

			fajrTime.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/Roboto-Regular.ttf"));
			fajrTime.setText(TimeHelper.getTimeWithoutSeconds(prayersList.get(0)));
			duhrTime.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/Roboto-Regular.ttf"));
			duhrTime.setText(TimeHelper.getTimeWithoutSeconds(prayersList.get(1)));
			asrTime.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/Roboto-Regular.ttf"));
			asrTime.setText(TimeHelper.getTimeWithoutSeconds(prayersList.get(2)));
			magribTime.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/Roboto-Regular.ttf"));
			magribTime.setText(TimeHelper.getTimeWithoutSeconds(prayersList.get(3)));
			ishaTime.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/Roboto-Regular.ttf"));
			ishaTime.setText(TimeHelper.getTimeWithoutSeconds(prayersList.get(4)));
		
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
			    							
			    						} 
			                        }
			               });
			        }};

			
			// start the timer
			// 60000 ms == 60 seconds == 1 minutes :)
			myTimer.schedule(scanTask, 0, 60000); 
			
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//Log.e("tomaanina",e.getCause() + " : " + e.getMessage());
		}

		
	}
	
	public void updateRemainingTime(int yy, int mm, int dd ) throws IOException{
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
		int def =  TimeHelper.different((h*3600+m*60+s),time);
		remainingTime.setText(TimeHelper.secondsToTime(def));	
		remainingTime.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/Roboto-Regular.ttf"));
	}

	// add main menu items
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, getString(R.string.settings));
		menu.add(0, 3, 3, getString(R.string.about));
		// find the current city automatically
		menu.add(0, 4, 4, getString(R.string.autoCityTitle));
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
			//new AutoCityMainActivity(this, dialog).startSearch();
			//return true;
		case 5:
			// run City Finder Activity
			Intent cityFinderActivity = new Intent (this , CityFinder.class);
			startActivity(cityFinderActivity);
			
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
		
		Intent cityFinderActivity = new Intent (this , CityFinder.class);
		startActivity(cityFinderActivity);
		
	}
	


}