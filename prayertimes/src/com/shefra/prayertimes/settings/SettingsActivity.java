package com.shefra.prayertimes.settings;

import java.io.IOException;
import java.util.List;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.*;

import android.os.Bundle;
import android.preference.*;

public class SettingsActivity extends PreferenceActivity {
	Manager m;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		m = new Manager(getApplicationContext());
		try {
			
			this.init();
		} catch (Exception e) {
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

	@Override
	protected void onResume() {
		super.onResume();
		this.init();
	}

	private void init() {
		
		//fill city list and country list
		ListPreference countryPreference = (ListPreference) findPreference("country");
		ListPreference cityPreference = (ListPreference) findPreference("city");
		
		CountryListener countryListener = new CountryListener(cityPreference,m);
		countryPreference.setOnPreferenceChangeListener(countryListener);
		
		CityListener cityListener = new CityListener(cityPreference,m);
		cityPreference.setOnPreferenceChangeListener(cityListener);
		
		this.fillCountryPreference(countryPreference);
		String v = countryPreference.getValue();
		if(v == null)
			v = "1";//TODO 
		CityListener.fillCityPreference(cityPreference,v ,m);

		// let Manager store related attributes such as city latitude , city zone ...
		settingAttributes sa = new settingAttributes();
		String cityId = cityPreference.getValue();
		sa.city.cityNo = -1;
		if (cityId != null) {
			sa.city.cityNo = Integer.parseInt(cityId);
		}
		if (sa.city.cityNo == -1)
			sa.city.cityNo = 1;
		m.setSetting(sa);

		//
		ListPreference ls = (ListPreference) findPreference("language");
		CharSequence[] entries = { "English", "Arabic" };
		CharSequence[] entryValues = { "1", "2" };
		ls.setEntries(entries);
		ls.setDefaultValue("1");
		ls.setEntryValues(entryValues);

		//
		ListPreference sessionList = (ListPreference) findPreference("session");
		CharSequence[] sessionEntries = { "Winter", "Summer" };
		CharSequence[] sessionEntryValues = { "1", "2" };
		ls.setEntries(sessionEntries);
		ls.setDefaultValue("1");
		ls.setEntryValues(sessionEntryValues);

		//
		ListPreference ssLP = (ListPreference) findPreference("silentStart");
		CharSequence[] ssEntries = { "immedialtely", "10 minutes","20 minutes" };
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

	}
	


}