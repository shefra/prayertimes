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


// this is custom class that inherits from DialogPreference . 
// it used when the auto city finder feature is used on Settings Screen
// Read more about DialogPreference to get more about this way
public class AutoCityDialogPreference extends DialogPreference implements
		LocationListener {
	
	// progress dialog used to display waiting dialog
	// read more on Android doc
	ProgressDialog dialog;
	// used to start/stop GPS/Network provider
	LocationManager locManager;

	public AutoCityDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

	} 
 
	// when the dialog closed .. start search the current city
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		// if the user press OK button
		if (positiveResult) {
			// If the network provider works run it , else try GPS  provider
			// TODO : what happens if GPS and Network providers are not suuported ??
			Context context = this.getContext();
			locManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
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
			
			// start waiting dialog
			dialog = new ProgressDialog(context);
			dialog.setTitle("");
			dialog.setMessage(context.getString(R.string.pleaseWait));
			dialog.setButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() 
		    {
		        public void onClick(DialogInterface dialog, int which) 
		        {
		        	// if cancel button is clicked just remore this listener
		        	// and hide the waiting dialog
		        	locManager.removeUpdates(AutoCityDialogPreference.this);
		        	dialog.dismiss();
		            return;
		        }
		    });
			// work fine until this line ? show waiting screen
			// TODO : use thread
			dialog.show();
		} else {
			// this.setSummary("NO");
		}
	}

	// Location Listener implementation
	// read Android doc for more info
	// this methods is triggered when new location ( latitiude and longitude ) is found by the system
	private void updateWithNewLocation(Location location) {
		String latLongString = "";
		Manager manager = new Manager(this.getContext());
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			
			manager.findCurrentCity(lat,lng);

		} else {
			this.setSummary( this.getContext().getString(R.string.noLocationFound) );
		}

		// ok , we don't need this listener anymore :) 
		locManager.removeUpdates(this);
		// and hide the waiting dialog
		dialog.hide();
		
	}

	// read Android Docs
	public void onLocationChanged(Location location) {
		updateWithNewLocation(location);
	}

	// read Android Docs
	public void onProviderDisabled(String provider) {
		updateWithNewLocation(null);
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
