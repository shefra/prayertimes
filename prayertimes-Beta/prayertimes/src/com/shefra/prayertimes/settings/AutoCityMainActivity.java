package com.shefra.prayertimes.settings;

import java.util.ArrayList;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.settingAttributes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

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
				dialog.setMessage(context.getString(R.string.pleaseWait));
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

	} 
	
	// Location Listener implementation
	// read Android doc for more info
	// this methods is triggered when new location ( latitiude and longitude ) is found by the system
	private void updateWithNewLocation(Location location) {
		
		Manager manager = new Manager(context);
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			
			manager.findCurrentCity(lat,lng);
			// update main activity since the city name is changed
			// just update all views  
			MainActivity mainActivity = (MainActivity) context;
			mainActivity.init();

		} else {
			//this.setSummary( "No location found" );
		}
		// remove the listener , we don't need it anymore
		locManager.removeUpdates(this);
		dialog.hide();
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
