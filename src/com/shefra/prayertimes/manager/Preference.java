package com.shefra.prayertimes.manager;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preference {

	private Context context;
	public String mazhab = null;
	public String calender = null;
	public String season = null;
	public City city = null;

	public static String  DEFAULT_COUNTRY_ID   = "211" ; // SA
	public static String  DEFAULT_COUNTRY_NAME = "Saudi Arabia" ; // SA
	public static String  DEFAULT_CITY_ID = "14244";
	public static String  DEFAULT_CITY_NAME = "Makkah";
	public static Integer DEFAULT_TIMEZONE  = 3;
	public static String  DEFAULT_LATITUDE  = "21.4300003051758";
	public static String  DEFAULT_LONGITUDE = "39.8199996948242";
	public static String  DEFAULT_CALENDAR = "UmmAlQuraUniv"; 
	public static String  DEFAULT_MAZHAB = "Default"; 
	public static String  DEFAULT_SEASON = "Winter"; 
	public static Integer DEFAULT_SILENT_DURATION = 20 * 60 * 1000;
	public static Integer DEFAULT_SILENT_START= 2 * 60 * 1000;
	public Preference(Context context) {
		this.context = context;
	}

	public void fetchCurrentPreferences() {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		this.city = new City();
		this.city.name = (String) preferences.getString("cityName",DEFAULT_CITY_NAME);
		this.city.id = (String) preferences.getString("cityNo",DEFAULT_CITY_ID);
		this.city.country.name = (String) preferences.getString("countryName",DEFAULT_COUNTRY_NAME);
		this.city.country.id = (String) preferences.getString("countryNo",DEFAULT_COUNTRY_ID);
		this.city.timeZone = (Integer) preferences.getInt("timeZone",DEFAULT_TIMEZONE);
		this.city.latitude = (String) preferences.getString("latitude", DEFAULT_LATITUDE);
		this.city.longitude = (String) preferences.getString("longitude", DEFAULT_LONGITUDE);
		this.calender = (String) preferences.getString("calendar",DEFAULT_CALENDAR);
		this.mazhab = (String) preferences.getString("mazhab",DEFAULT_MAZHAB);
		this.season = (String) preferences.getString("season",DEFAULT_SEASON);

	}
	
	public boolean isFirstStart(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean firstStart = pref.getBoolean("firstStart",true);
		return firstStart;
	}

	public void setFirstStart(boolean firstStart) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = pref.edit();
    	edit.putBoolean("firstStart", firstStart);
    	edit.commit();		
	}

	public void setCityName(String name) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = pref.edit();
    	edit.putString("cityName", name);
    	edit.commit();			
	}

	public void setLongitude(String longitude) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = pref.edit();
    	edit.putString("longitude", longitude);
    	edit.commit();		
	}

	public void setLatitude(String latitude) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = pref.edit();
    	edit.putString("latitude", latitude);
    	edit.commit();	
	}

	public void setCountryName(String name) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = pref.edit();
    	edit.putString("countryName", name);
    	edit.commit();			
	}
	

	public void setTimeZone(Integer timeZone) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = pref.edit();
    	edit.putInt("timeZone", timeZone);
    	edit.commit();			
	}

	public int getSilentDuration() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.parseInt(pref.getString("silentDuration",Integer.toString(DEFAULT_SILENT_DURATION))) * 60 * 1000;
	}

	public void setCityNo(String id) {
		if(id == null)
			return;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = pref.edit();
    	edit.putString("cityNo", id);
    	edit.commit();			
		
	}
	
	public void setCountryNo(String id) {
		if(id == null)
			return;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = pref.edit();
    	edit.putString("countryNo", id);
    	edit.commit();			
		
	}
	
	public boolean isAutoSilentDisabled(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("disable",false);
	}
	
	public int getSilentStart(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.parseInt(pref.getString("silentStart",Integer.toString(DEFAULT_SILENT_START))) * 60 * 1000;		
	}

	public Map<String, ?> getAll(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getAll();
	}
	
}
