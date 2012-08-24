package com.shefra.prayertimes.manager;

import com.shefra.prayertimes.activity.CityFinder;
import com.shefra.prayertimes.activity.CityFinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

public class CityLocationListener implements LocationListener {
	LocationManager locManager;
	Context context;
	private Integer objectType;

	public CityLocationListener(Context context, Integer objectType) {
		this.context = context;
		this.objectType = objectType;
	}

	public void startSearch() {
		try {
			locManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

			// If the network provider works run it , else try GPS provider
			// TODO : what will happen if GPS and Network providers are not
			// supported ?

			if (!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				// the last parameter is Location Listener which is this object
				// since we implement LocationListener Interface
				// read more on Android
				if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					locManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 0, 0, this);
				} else {
					CityFinder finder = (CityFinder) context;
					finder.onSearchStopped(null);
				}
			} else {
				// the last parameter is Location Listener which is this object
				// since we implement LocationListener Interface
				// read more on Android
				locManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0, this);

			}
		} catch (Exception e) {
			e = e;

		}

	}

	// private int updateLocation(Location location) {
	// if (location != null) {
	// final double lat = location.getLatitude();
	// final double lng = location.getLongitude();
	//
	// try {
	// Manager manager = new Manager(context);
	// manager.findCurrentCity(lat, lng);
	// // update main activity since the city name is changed
	// // just update all views
	//
	// } catch (Exception e) {
	// return -1;
	//
	// }
	//
	// }
	// locManager.removeUpdates(this);
	// CityFinder cityFinder = (CityFinder) context;
	// cityFinder.stopSearch();
	// return 0 ; // successful
	// }

	public void stopSearch() {
		if (locManager != null)
			locManager.removeUpdates(this); 
	}

	// Location Listener implementation
	// read Android doc for more info
	// this methods is triggered when new location ( latitiude and longitude )
	// is found by the system
	public void updateWithNewLocation(Location location) {
		String latLongString = "";

		if (location != null) {
			double lat = location.getLatitude(); 
			double lng = location.getLongitude();

		}

		// ok , we don't need this listener anymore :)
		this.stopSearch();
		// and hide the waiting dialog
		CityFinder finder = (CityFinder) context;
		finder.onSearchStopped(location);

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
		provider = provider ;
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		provider = provider ;
	}

}