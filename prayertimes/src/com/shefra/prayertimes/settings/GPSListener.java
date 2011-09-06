package com.shefra.prayertimes.settings;

import java.util.ArrayList;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.settingAttributes;

import android.app.ProgressDialog;
import android.content.Context;
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

public class GPSListener implements LocationListener {
	ProgressDialog dialog;
	LocationManager locManager;
	Context context;

	public GPSListener(Context c,ProgressDialog dialog) {
		context = c;
		this.dialog = dialog;
		locManager = (LocationManager) context
		.getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
		0, this);
	}
 
	// Location Listener implementation
	private void updateWithNewLocation(Location location) {
		String latLongString = "";

		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			Manager manager = new Manager(context);
			manager.findCurrentCity(lat, lng);

		} else {

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
