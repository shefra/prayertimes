package com.shefra.prayertimes.manager;

import com.shefra.prayertimes.helper.DatabaseHelper;

import android.preference.ListPreference;
import android.preference.Preference;



//this class works as country listener
//it used by country list on Settings screen
//when the country is changed manually by the user 
//this class start working
public class CountryListener implements
		android.preference.Preference.OnPreferenceChangeListener {
	private Manager manager;
	private ListPreference cityList;
	private DatabaseHelper databaseHelper;
	public CountryListener(ListPreference cityList,Manager manager){
		this.manager = manager;
		this.cityList = cityList;
		databaseHelper = new DatabaseHelper(manager.getContext());
	}
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		
		// when the country change , update city list with new cities
		// don't worry about country id 
		// the system will write it to Preference file ( xml file)
		// since we use Standard way ( thanks preference ) 
		CityListener.fillCityPreference(cityList, (String)newValue,databaseHelper);
		return true;
	}
	

}   