package com.shefra.prayertimes.settings;

import java.util.ArrayList;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.Manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;

public class MyDialogPreference extends DialogPreference implements
		LocationListener {
	ProgressDialog dialog;

	public MyDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// this.setLayoutResource(R.layout.city);

	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			LocationManager locManager;
			Context context = this.getContext();
			locManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, this);
			dialog = ProgressDialog.show(context, "",
					"Please wait for few seconds...", true);

		} else {
			// this.setSummary("NO");
		}
	}

	// Location Listener implementation
	private void updateWithNewLocation(Location location) {
		String latLongString = "";
		
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			findCurrentCity(lat,lng);

		} else {
			this.setSummary( "No location found" );
		}
		
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

	public void findCurrentCity(double latitude, double longitude) {
		double min = 0;
		int i = 0, pos = 0;
		Manager manager = new Manager(this.getContext());
		ArrayList<City> cityList = manager.getCityList(-1);
		for (City city : cityList) {
			double lat = Double.parseDouble(city.latitude);
			double lon = Double.parseDouble(city.longitude);
			double pk = (180 / 3.14159);
			double a1 = (lat / pk);
			double a2 = (lon / pk);

			double b1 = (latitude/ pk);
			double b2 = (longitude / pk);

			double t1 = (Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math
					.cos(b2));
			double t2 = (Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math
					.sin(b2));
			double t3 = (Math.sin(a1) * Math.sin(b1));
			double tt = Math.acos(t1 + t2 + t3);
			double dist = (6366000 * tt);
			if (dist < min || i == 0) {
				min = dist;
				pos = i;
			}
			i++;

		}
		this.setSummary(cityList.get(pos).cityName);
	}
}
