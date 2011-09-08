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

public class AutoCityDialogPreference extends DialogPreference implements
		LocationListener {
	ProgressDialog dialog;
	LocationManager locManager;

	public AutoCityDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// this.setLayoutResource(R.layout.city);
		// this.setDialogMessage(this.getContext().getString(R.string.dialogAutoSearchMessage));

	} 
 
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			
			Context context = this.getContext();
			locManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
						0, this);
			else
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
						0, this);
			dialog = new ProgressDialog(context);
			dialog.setTitle("");
			dialog.setMessage(context.getString(R.string.pleaseWait));
			dialog.setButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() 
		    {
		        public void onClick(DialogInterface dialog, int which) 
		        {
		        	locManager.removeUpdates(AutoCityDialogPreference.this);
		        	dialog.dismiss();
		            return;
		        }
		    });
			dialog.show();
		} else {
			// this.setSummary("NO");
		}
	}

	// Location Listener implementation
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
