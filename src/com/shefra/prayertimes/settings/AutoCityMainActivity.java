package com.shefra.prayertimes.settings;


import java.util.ArrayList;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.SettingAttributes;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

// this class works exactly as AutoCityDialogPreference
// but since I faced some bugs 
// I seprate the two class
// this class is used to find current city and work as LocationListener
// it used at two stages in our app : 
// 
// 1- at the first run for the app on this device
// 2- when the user clicks on : find my city menu item from main menu 
public class AutoCityMainActivity implements LocationListener{
	ProgressDialog dialog;
	LocationManager locManager;
	Context context;
	private Handler handler;
	
	public AutoCityMainActivity(Context context,ProgressDialog dialog){
		
		this.context = context;
		this.dialog = dialog;
	}  
	
	public void startSearch() {

		locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		// If the network provider works run it , else try GPS  provider
		// TODO : what happens if GPS and Network providers are not suuported ??

						
			if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) )
				// the last parameter is Location Listener which is this object
				// since we implement LocationListener Interface
				// read more on Android
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
						0, this);
			else
				// the last parameter is Location Listener which is this object
				// since we implement LocationListener Interface
				// read more on Android
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
						0, this);
			
				dialog = new ProgressDialog(context);
				dialog.setTitle("");
				dialog.setMessage(("WWWW"));
				dialog.setButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() 
			    {
			        public void onClick(DialogInterface dialog, int which) 
			        {
			        	locManager.removeUpdates(AutoCityMainActivity.this);
			        	dialog.dismiss();
			            return;
			        }
			    });

			dialog.show();
			handler = new Handler();

	} 
	
	// Location Listener implementation
	// read Android doc for more info
	// this methods is triggered when new location ( latitiude and longitude ) is found by the system
	private void updateWithNewLocation(Location location) {
		
		
		if (location != null) {
			final double lat = location.getLatitude();
			final double lng = location.getLongitude();
			
			// Do something long
			Runnable runnable = new Runnable() {
				
				public void run() {
					
						try {
							Manager manager = new Manager(context);
							manager.findCurrentCity(lat,lng);
							// update main activity since the city name is changed
							// just update all views  

							
						} catch (Exception e) {
							e.printStackTrace();
						}
						handler.post(new Runnable() {
						
							public void run() {
								if (AutoCityMainActivity.this.dialog.isShowing()) {
									AutoCityMainActivity.this.dialog.dismiss();
								}
								MainActivity mainActivity = (MainActivity) context;
								mainActivity.init();
							}
						});
					
				}
			};
			new Thread(runnable).start();

		} else {
			//this.setSummary( "No location found" );
		}
		
		// remove the listener , we don't need it anymore
		locManager.removeUpdates(AutoCityMainActivity.this);
	}

	public void onLocationChanged(Location location) {
		updateWithNewLocation(location);
	}

	public void onProviderDisabled(String provider) {
		updateWithNewLocation(null);
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}



}
