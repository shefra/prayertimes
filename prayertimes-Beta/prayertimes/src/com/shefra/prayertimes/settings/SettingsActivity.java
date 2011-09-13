package com.shefra.prayertimes.settings;

import java.util.List;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.*;
import android.widget.Toast;

// this class is the main class for Settings screen
// It used PreferenceActivity to do the job
// read more on Android Doc about Preference 
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener  {
	Manager m;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		// initialized the Settings activity with new values
		this.init();

	} 
	public void init(){
		m = new Manager(getApplicationContext());


		try {
		
			// country List preference assigned with Country listener 
			// to get know when the country is changed
			// the same thing with city preference
			ListPreference countryPreference = (ListPreference) findPreference("country");
			ListPreference cityPreference = (ListPreference) findPreference("city");
			CountryListener countryListener = new CountryListener(cityPreference,m);
			countryPreference.setOnPreferenceChangeListener(countryListener);
			CityListener cityListener = new CityListener(cityPreference,m);
			cityPreference.setOnPreferenceChangeListener(cityListener);
			
			// fill country preference list with country list 
			fillCountryPreference(countryPreference);
			String v = countryPreference.getValue();
			if(v == null)
				v = "1";//TODO 
			CityListener.fillCityPreference(cityPreference,v ,m);

			// ok , now let us set summary sections for each preference
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			
			String countryId = pref.getString("country", "211"); //TODO: check default value/ the second parameter		
			countryPreference.setSummary(m.getCountry(Integer.parseInt(countryId)).countryName);
			
			
			String cityId = pref.getString("city", "1"); // TODO : check default value/ second parameter			
			cityPreference.setSummary(m.getCity(Integer.parseInt(cityId)).cityName);

			// set Summary text for each element in the setting screen
			// Read more about summary code on Android Docs!
			// It used to display the selected value ( current value ) under the element
			// e.g. if the current country "XYZ" the it displays XYZ under the element
			// it reads the value from xml ( preference file) and uses Manager helper functions as well
			PreferenceScreen ps = (PreferenceScreen) findPreference("first_preferencescreen");			
			ps.setSummary(m.getCountry(Integer.parseInt(countryId)).countryName + "/" + m.getCity(Integer.parseInt(cityId)).cityName);
			
			ListPreference ssLP = (ListPreference) findPreference("silentStart");
			String silentStart = ssLP.getEntry().toString(); 
			ssLP.setSummary(silentStart);
			
			ListPreference sdLP = (ListPreference) findPreference("silentDuration");
			String silentDuration = sdLP.getEntry().toString();
			sdLP.setSummary(silentDuration);		
			
			ListPreference nsLP = (ListPreference) findPreference("notSound");
			String nsString = nsLP.getEntry().toString();
			nsLP.setSummary(nsString);			
			

			ListPreference mazhabP = (ListPreference) findPreference("mazhab");
			String mazhabSummary = mazhabP.getEntry().toString();
			mazhabP.setSummary(mazhabSummary);	
			 
			ListPreference seasonP = (ListPreference) findPreference("season");
			String seasonSummary = seasonP.getEntry().toString();
			seasonP.setSummary(seasonSummary);		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void on(android.widget.ListView l, android.view.View v,
			int position, long id) {

	};
 
	// read data from database into preference list 
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
		countryPref.setDefaultValue("211");
		countryPref.setEntryValues(countryEntryValues);

	}
	
	protected void onResume() {
	    super.onResume();
	    this.init();
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
	}

	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	    this.init();
	}



}