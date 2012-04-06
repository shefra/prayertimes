package com.shefra.prayertimes.manager;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preference {

	private Context context;
	public String mazhab = null;
	public String calender = null;
	public String season = null;
	public City city = null;

	public static String  DEFAULT_COUNTRY_ID   = "221" ; // SA
	public static String  DEFAULT_COUNTRY_NAME = "SA" ; // SA
	public static String  DEFAULT_CITY_ID = "14244";
	public static String  DEFAULT_CITY_NAME = "Makkah";
	public static Integer DEFAULT_TIMEZONE = 3;
	public static String  DEFAULT_CALENDAR = "UmmAlQuraUniv"; 
	public Preference(Context context) {
		this.context = context;
	}

	public void fetchCurrentPreferences() {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Map<String, ?> preferenceValues = preferences.getAll();
		this.city = new City();
		this.city.name = (String) preferences.getString("cityName",DEFAULT_CITY_NAME);
		this.city.id = (String) preferences.getString("cityNo",DEFAULT_CITY_ID);
		this.city.country.name = (String) preferences.getString("countryName",DEFAULT_COUNTRY_NAME);
		this.city.country.id = (String) preferences.getString("countryNo",DEFAULT_COUNTRY_ID);
		this.city.timeZone = (Integer) preferences.getInt("timeZone",DEFAULT_TIMEZONE);
		this.calender = (String) preferences.getString("calendar",);
		this.mazhab = (String) preferences.getString("mazhab");
		this.season = (String) preferences.getString("season");

	}
}
