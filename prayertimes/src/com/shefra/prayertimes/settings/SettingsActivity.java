package com.shefra.prayertimes.settings;

import java.io.IOException;
import java.util.List;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.*;

import android.os.Bundle;
import android.preference.*;

class CountryListener implements
		android.preference.Preference.OnPreferenceChangeListener {
	private Manager manager;
	private ListPreference cityList;
	public CountryListener(ListPreference cityList,Manager manager){
		this.manager = manager;
		this.cityList = cityList;
	}
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		ListPreference lp = (ListPreference) preference;
		String value = (String)newValue;
		
		CountryListener.fillCityPreference(cityList, value,manager);
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

public class SettingsActivity extends PreferenceActivity {
	Manager m;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		m = new Manager(getApplicationContext());
		try {
			m.createDatabase();

			ListPreference countryPreference = (ListPreference) findPreference("country");
			ListPreference cityPreference = (ListPreference) findPreference("city");
			CountryListener lis = new CountryListener(cityPreference,m);
			countryPreference.setOnPreferenceChangeListener(lis);
			
			fillCountryPreference(countryPreference);
			String v = countryPreference.getValue();
			if(v == null)
				v = "1";//TODO 
			CountryListener.fillCityPreference(cityPreference,v ,m);

			settingAttributes sa = new settingAttributes();
			String cityId = cityPreference.getValue();
			sa.city.cityNo = -1;
			if (cityId != null) {
				sa.city.cityNo = Integer.parseInt(cityId);
			}
			if (sa.city.cityNo == -1)
				sa.city.cityNo = 1;
			m.setSetting(sa);

			ListPreference ls = (ListPreference) findPreference("language");
			CharSequence[] entries = { "English", "Arabic" };
			CharSequence[] entryValues = { "1", "2" };
			ls.setEntries(entries);
			ls.setDefaultValue("1");
			ls.setEntryValues(entryValues);

			//
			ListPreference ssLP = (ListPreference) findPreference("silentStart");
			CharSequence[] ssEntries = { "immedialtely", "10 minutes",
					"20 minutes" };
			CharSequence[] ssEntryValues = { "0", "10", "20" };
			ssLP.setEntries(ssEntries);
			ssLP.setDefaultValue("1");
			ssLP.setEntryValues(ssEntryValues);

			//
			ListPreference sdLP = (ListPreference) findPreference("silentDuration");
			CharSequence[] sdEntries = { "10 minutes", "20 minutes",
					"30 minutes" };
			CharSequence[] sdEntryValues = { "10", "20", "30" };
			sdLP.setEntries(sdEntries);
			sdLP.setDefaultValue("1");
			sdLP.setEntryValues(sdEntryValues);



		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void on(android.widget.ListView l, android.view.View v,
			int position, long id) {

	};

	private void fillCountryPreference(ListPreference countryPref) {
		List<Country> countryList = m.getCountryList();
		CharSequence[] countryEntries = new CharSequence[countryList.size()];
		CharSequence[] countryEntryValues = new CharSequence[countryList.size()];
		int i = 0;
		for (Country c : countryList) {
			countryEntries[i] = c.countryName;
			countryEntryValues[i] = Integer.toString(c.countryNo);
			i++;
		}
		countryPref.setEntries(countryEntries);
		countryPref.setDefaultValue("1");
		countryPref.setEntryValues(countryEntryValues);

	}


}