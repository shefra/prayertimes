package com.shefra.prayertimes.settings;
import java.util.List;

import android.content.Intent;
import android.preference.ListPreference;
import android.preference.Preference;

import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.settingAttributes;
import com.shefra.prayertimes.services.ServiceSetAlarm;

public class CityListener implements
		android.preference.Preference.OnPreferenceChangeListener {
	private Manager manager;
	//private ListPreference cityList;
	public CityListener(ListPreference cityList,Manager manager){
		this.manager = manager;
		//this.cityList = cityList;
	} 
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		//ListPreference lp = (ListPreference) preference;
		//String value = (String)newValue;
		settingAttributes sa = new settingAttributes();
		String cityId = (String)newValue;
		sa.city.cityNo = -1;
		if (cityId != null) {
			sa.city.cityNo = Integer.parseInt(cityId);
		}
		if (sa.city.cityNo == -1)
			sa.city.cityNo = 1;
		manager.setSetting(sa);
		
		Intent intent = new Intent(manager.getContext(),ServiceSetAlarm.class);
		manager.getContext().startService(intent);
		
		
		
		
		return true;
	}
	
	public static void fillCityPreference(ListPreference cityPref, String countryId,Manager m) {
		List<City> cityList = m.getCityList(Integer.parseInt(countryId));
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