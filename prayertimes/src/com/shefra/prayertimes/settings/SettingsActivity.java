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

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener  {
	Manager m;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		this.init();

	} 
	public void init(){
		m = new Manager(getApplicationContext());


		try {
		

			ListPreference countryPreference = (ListPreference) findPreference("country");
			ListPreference cityPreference = (ListPreference) findPreference("city");
			CountryListener countryListener = new CountryListener(cityPreference,m);
			countryPreference.setOnPreferenceChangeListener(countryListener);
			CityListener cityListener = new CityListener(cityPreference,m);
			cityPreference.setOnPreferenceChangeListener(cityListener);
			/*final CheckBoxPreference autoCityPreference = (CheckBoxPreference) findPreference("autocity");
			AutoCityListener autoCityListener = new AutoCityListener(cityPreference,m,(LocationManager)getSystemService(Context.LOCATION_SERVICE));
			autoCityPreference.setOnPreferenceChangeListener(autoCityListener);*/


			fillCountryPreference(countryPreference);
			String v = countryPreference.getValue();
			if(v == null)
				v = "1";//TODO 
			CityListener.fillCityPreference(cityPreference,v ,m);
			
			
			


			//
			ListPreference ssLP = (ListPreference) findPreference("silentStart");
			CharSequence[] ssEntries = { "@string/now", "@string/minutes10",
					"@string/minutes20" };
			CharSequence[] ssEntryValues = { "0", "10", "20" };
			ssLP.setEntries(ssEntries);
			ssLP.setDefaultValue("1");
			ssLP.setEntryValues(ssEntryValues);

			//
			ListPreference sdLP = (ListPreference) findPreference("silentDuration");
			CharSequence[] sdEntries = { "@string/minutes10", "@string/minutes20",
					"@string/minutes30" };
			CharSequence[] sdEntryValues = { "10", "20", "30" };
			sdLP.setEntries(sdEntries);
			sdLP.setDefaultValue("1");
			sdLP.setEntryValues(sdEntryValues);

			// ok , now let us set summary sections for each preference
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			
			String countryId = pref.getString("country", "1"); //TODO: check default value/ the second parameter		
			countryPreference.setSummary(m.getCountry(Integer.parseInt(countryId)).countryName);
			
			
			String cityId = pref.getString("city", "1"); // TODO : check default value/ second parameter			
			cityPreference.setSummary(m.getCity(Integer.parseInt(cityId)).cityName);

			PreferenceScreen ps = (PreferenceScreen) findPreference("first_preferencescreen");			
			ps.setSummary(m.getCountry(Integer.parseInt(countryId)).countryName + "/" + m.getCity(Integer.parseInt(cityId)).cityName);
			
		
			String silentStart = ssLP.getEntry().toString(); 
			ssLP.setSummary(silentStart);
			
			String silentDuration = sdLP.getEntry().toString();
			sdLP.setSummary(silentDuration);			

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