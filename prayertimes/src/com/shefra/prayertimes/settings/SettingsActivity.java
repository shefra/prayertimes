package com.shefra.prayertimes.settings;

import java.io.IOException;
import java.util.List;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.*;

import android.os.Bundle;
import android.preference.*;

public class SettingsActivity extends PreferenceActivity {
    Manager m ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        addPreferencesFromResource(R.xml.settings);
        
        m = new Manager(getApplicationContext());
        try {
			m.createDatabase();
		
        ListPreference countryPreference = (ListPreference)findPreference("country");
        fillCountryPreference(countryPreference);


        ListPreference cityPreference = (ListPreference)findPreference("city");
        fillCityPreference(cityPreference,countryPreference.getValue());

        settingAttributes sa = new settingAttributes();
        sa.city.cityNo = -1;
        if(cityPreference != null)
        	sa.city.cityNo = Integer.parseInt(cityPreference.getValue());
        if(sa.city.cityNo == -1)
        	sa.city.cityNo = 1;
        m.setSetting(sa);
        
        ListPreference ls = (ListPreference)findPreference("language");
        CharSequence[] entries = {"English","Arabic"};
        CharSequence[] entryValues = {"1","2"};
        ls.setEntries(entries);
        ls.setDefaultValue("1");
        ls.setEntryValues(entryValues);
        
        //
        ListPreference ssLP = (ListPreference)findPreference("silentStart");
        CharSequence[] ssEntries = {"immedialtely","10 minutes" , "20 minutes"};
        CharSequence[] ssEntryValues = {"0","10","20"};
        ssLP.setEntries(ssEntries);
        ssLP.setDefaultValue("1");
        ssLP.setEntryValues(ssEntryValues);
        
        //
        ListPreference sdLP = (ListPreference)findPreference("silentDuration");
        CharSequence[] sdEntries = {"10 minutes","20 minutes" , "30 minutes"};
        CharSequence[] sdEntryValues = {"10","20","30"};
        sdLP.setEntries(sdEntries);
        sdLP.setDefaultValue("1");
        sdLP.setEntryValues(sdEntryValues);
        
    }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }

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
	
	private void fillCityPreference(ListPreference cityPref,String countryId) {
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
        cityPref.setEntryValues(cityEntryValues);}
}