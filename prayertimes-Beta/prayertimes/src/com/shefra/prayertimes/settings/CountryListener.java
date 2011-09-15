package com.shefra.prayertimes.settings;
import android.preference.ListPreference;
import android.preference.Preference;

import com.shefra.prayertimes.manager.Manager;


//this class works as country listener
//it used by country list on Settings screen
//when the country is changed manually by the user 
//this class start working
public class CountryListener implements
		android.preference.Preference.OnPreferenceChangeListener {
	private Manager manager;
	private ListPreference cityList;
	public CountryListener(ListPreference cityList,Manager manager){
		this.manager = manager;
		this.cityList = cityList;
	}
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		
		// when the country change , update city list with new cities
		// don't worry about country id 
		// the system will write it to Preference file ( xml file)
		// since we use Standard way ( thanks preference ) 
		CityListener.fillCityPreference(cityList, (String)newValue,manager);
		return true;
	}
	

}  