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
		
	
						
			if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) )
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
						0, this);
			else
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
						0, this);
			
				dialog = new ProgressDialog(context);
				dialog.setTitle("");
				dialog.setMessage("Please wait for few seconds...");
				dialog.setButton("cancel", new DialogInterface.OnClickListener() 
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
	private void updateWithNewLocation(Location location) {
		
		Manager manager = new Manager(context);
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			
			manager.findCurrentCity(lat,lng);
			MainActivity mainActivity = (MainActivity) context;
			mainActivity.init();

		} else {
			//this.setSummary( "No location found" );
		}
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
