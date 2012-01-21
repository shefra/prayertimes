package com.shefra.prayertimes.settings;


import java.util.List;

import android.content.Intent;
import android.preference.ListPreference;
import android.preference.Preference;

import com.shefra.prayertimes.helper.DatabaseHelper;
import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.SettingAttributes;
import com.shefra.prayertimes.services.ServiceSetAlarm;

// this class works as city listener
// it used by city list on Settings screen
// when the city is changed manually by the user 
// this class start working
public class CityListener implements
		android.preference.Preference.OnPreferenceChangeListener {
	private Manager manager;
	
	public CityListener(ListPreference cityList,Manager manager){
		this.manager = manager;
		
	} 

	// this methods is triggered by the system when the city changed
	// it gives us the new city value , whitch is city id 
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		SettingAttributes sa = new SettingAttributes();
		String cityId = (String)newValue;
		sa.city.cityNo = -1;
		if (cityId != null) {
			sa.city.cityNo = Integer.parseInt(cityId);
		}
		if (sa.city.cityNo == -1)
			sa.city.cityNo = 1;
		
		// update preference file ( xml/setting file ) 
		manager.setSetting(sa);
		
		// restart the service .. read ServiceSetAlarm for more
		Intent intent = new Intent(manager.getContext(),ServiceSetAlarm.class);
		manager.getContext().startService(intent);
		
		  
		
		
		return true;
	}
	
	// used to fill the ListPreference view that appears in setting screen
	// with city list that is read from the database
	// read the cities that belong to country id 
	public static void fillCityPreference(ListPreference cityPref, String countryId,DatabaseHelper databaseHelper) {
		List<City> cityList = databaseHelper.getCityList(Integer.parseInt(countryId));
		CharSequence[] cityEntries = new CharSequence[cityList.size()];
		CharSequence[] cityEntryValues = new CharSequence[cityList.size()];
		int i = 0;
		for (City c : cityList) {
			cityEntries[i] = c.cityName;
			cityEntryValues[i] = Integer.toString(c.cityNo);
			i++;
		}
		cityPref.setEntries(cityEntries);
		cityPref.setDefaultValue("1");
		cityPref.setEntryValues(cityEntryValues);
	}
	

}