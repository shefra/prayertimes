package com.shefra.prayertimes.settings;
import android.preference.ListPreference;
import android.preference.Preference;

import com.shefra.prayertimes.manager.Manager;

public class CountryListener implements
		android.preference.Preference.OnPreferenceChangeListener {
	private Manager manager;
	private ListPreference cityList;
	public CountryListener(ListPreference cityList,Manager manager){
		this.manager = manager;
		this.cityList = cityList;
	}
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		
		CityListener.fillCityPreference(cityList, (String)newValue,manager);
		return true;
	}
	

}  