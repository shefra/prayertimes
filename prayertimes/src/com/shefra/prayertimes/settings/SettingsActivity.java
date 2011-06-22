package com.shefra.prayertimes.settings;

import com.shefra.prayertimes.*;
import android.os.Bundle;
import android.preference.*;

public class SettingsActivity extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        addPreferencesFromResource(R.xml.settings);
        ListPreference countries = (ListPreference)findPreference("country");
        CharSequence[] countryEntries = {"KSA","USA","UK"};
        CharSequence[] countryEntryValues = {"1","2","3"};
        countries.setEntries(countryEntries);
        countries.setDefaultValue("1");
        countries.setEntryValues(countryEntryValues);

        ListPreference cities = (ListPreference)findPreference("city");
        CharSequence[] cityEntries = {"Riyadh","London","Cal"};
        CharSequence[] cityEntryValues = {"1","2","3"};
        cities.setEntries(cityEntries);
        cities.setDefaultValue("1");
        cities.setEntryValues(cityEntryValues);

        ListPreference ls = (ListPreference)findPreference("language");
        CharSequence[] entries = {"English","Arabic","France"};
        CharSequence[] entryValues = {"1","2","3"};
        ls.setEntries(entries);
        ls.setDefaultValue("1");
        ls.setEntryValues(entryValues);
        
        
        
    }
}