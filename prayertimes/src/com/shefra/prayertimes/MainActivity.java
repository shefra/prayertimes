package com.shefra.prayertimes;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import com.shefra.prayertimes.services.ServiceSetAlarm;
import com.shefra.prayertimes.settings.GPSListener;
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
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	LocationManager locManager;
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

	private void init() {
		
		Manager manager = new Manager(getApplicationContext());
		
		String cityName = manager.getCurrentCity().cityName;
		TextView cityTextView = (TextView) findViewById(R.id.cityName);
		cityTextView.setText(cityName);
		Date date = new Date();
		int dd = date.getDate();//calendar.get(Calendar.DAY_OF_MONTH);
		int mm = date.getMonth()+1;//7;//calendar.get(Calendar.MONTH+1);
		int yy = date.getYear()+1900;//calendar.get(Calendar.YEAR);
		int h = date.getHours();//calendar.get(Calendar.HOUR_OF_DAY);
		int m = date.getMinutes();//calendar.get(Calendar.MINUTE);
		int s = date.getSeconds();//calendar.get(Calendar.SECOND);

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

			TextView remainingTime = (TextView) findViewById(R.id.remainingTime);
			int time = manager.nearestPrayerTime(h, m,s, yy, mm, dd);
			int def =  manager.diffrent((h*3600+m*60+s),time);
			remainingTime.setText(Manager.secondsToTime(def));		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "Settings");
		menu.add(0, 3, 3, "About");
		menu.add(0, 4, 4, "Find Current City");
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
			
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
		    alertDialog.setTitle("About");  
		    alertDialog.setMessage("Shefra @2011");  
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener(){
		    public void onClick(DialogInterface dialog, int which) {  
		        return;  
		    }});
		    alertDialog.show();
			
			return true;
		case 4:
			
			Context context = getApplicationContext();
			ProgressDialog dialog;
			dialog = ProgressDialog.show(context, "",
					"Please wait for few seconds...", true);
			
			locManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

			GPSListener lis = new GPSListener(context,dialog);
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, lis);

			return true;
		}
		return super.onOptionsItemSelected(item);

	}
	
	public void onResume(){
		super.onResume();
		this.init();
	}
	
	public void onFirstStart(){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
	    alertDialog.setTitle("Auto City Search");  
	    alertDialog.setMessage("To find your current city automatically, enable GPS then come back and select 'Menu->Find Current City ' Option.");  
	    alertDialog.setButton("OK", new DialogInterface.OnClickListener(){
	    public void onClick(DialogInterface dialog, int which) {  
	    	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    	startActivityForResult(intent, 111);
  
	    }});
	    alertDialog.show();
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 111 && resultCode == 0){
            LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);  
            
            if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){  
                               
        		AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
        	    alertDialog.setTitle("Info");  
        	    alertDialog.setMessage("The GPS in Enabled now");  
        	    alertDialog.setButton("OK", new DialogInterface.OnClickListener(){
        	    public void onClick(DialogInterface dialog, int which) {  
        	    	return;
          
        	    }});
        	    alertDialog.show();
            }else{
        		AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
        	    alertDialog.setTitle("Info");  
        	    alertDialog.setMessage("The GPS in still disabled, try again!");  
        	    alertDialog.setButton("OK", new DialogInterface.OnClickListener(){
        	    public void onClick(DialogInterface dialog, int which) {  
        	   
          
        	    }});
        	    alertDialog.show();
            }
        }
    }

}