/*
 * Manager class is managing the connection between the ( Database | XML-Files ) 
 * and the ( Prayer Model | Setting Screen | Main Screen  )
 *  
 *  
 * these are the Main functionalities: ( #MAIN FUNCTION , %HOW DOES IT WORKS , $METHOD APPEARANCE "SERIALLY" ).
 * 	# Store City-Attributes.
 * 		% By the city-ID -> Gets City Attributes -> Stores the City Data in the XML. 
 * 		$ ( setSetting() -> getData() -> xmlWriter() ).
 * 
 * 	# Calculate Prayer-Times.
 * 		% By Reading the City Attributes -> Runs the Prayer-Model to get Prayer Times. 
 * 		$ ( xmlReader() -> getPrayerTimes() ).
 * 
 * 	# Find out Nearest-Prayer-Time.
 * 		% By Calculating Prayer-Times and Comparing current time with them to get the next one.
 * 		$ ( getPrayerTimes() -> nearestPrayerTime() ).
 * 
 * 	# Copy the Country DataBase to the Device -> TODO MAIN FUNCTION (ABDULLAH).
 * 		% TODO HOW DOES IT WORKS .
 * 		$ TODO METHOD APPEARANCE "SERIALLY" .
 * 
 * 	# Find Current City Location -> TODO MAIN FUNCTION (MOHAMMED).
 * 		% TODO HOW DOES IT WORKS .
 * 		$ TODO METHOD APPEARANCE "SERIALLY" .
 * 
 * #.
 * 		%.
 * 		$.
 */
package com.shefra.prayertimes.manager;

import helper.DatabaseHelper;
import helper.TimeHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.shefra.prayertimes.moazen.PrayerTime;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.*;
import android.preference.PreferenceManager;

// Manager is the main class that works as layer  between the app and database/xml files
public class Manager {

	private Context context;
	DatabaseHelper databaseHelper;

	public Manager(Context applicationContext) {
		
		this.context = applicationContext;
		databaseHelper = new DatabaseHelper(applicationContext);
	}

	// get nearest prayer time based on current time

	public int nearestPrayerTime(int hour, int min, int sec, int year,
			int month, int day) throws IOException {
		ArrayList<String> prayerTimes = getPrayerTimes(day, month, year);
		int[] prayerTimeInSeconds = new int[5];

		// Convert prayer times to seconds
		prayerTimeInSeconds[0] = TimeHelper.getSec(
				TimeHelper.to24(prayerTimes.get(0)),
				TimeHelper.getMinute(prayerTimes.get(0)),
				TimeHelper.getSecond(prayerTimes.get(0)));
		prayerTimeInSeconds[1] = TimeHelper.getSec(
				TimeHelper.to24(prayerTimes.get(1)),
				TimeHelper.getMinute(prayerTimes.get(1)),
				TimeHelper.getSecond(prayerTimes.get(1)));
		prayerTimeInSeconds[2] = TimeHelper.getSec(
				TimeHelper.to24(prayerTimes.get(2)),
				TimeHelper.getMinute(prayerTimes.get(2)),
				TimeHelper.getSecond(prayerTimes.get(2)));
		prayerTimeInSeconds[3] = TimeHelper.getSec(
				TimeHelper.to24(prayerTimes.get(3)),
				TimeHelper.getMinute(prayerTimes.get(3)),
				TimeHelper.getSecond(prayerTimes.get(3)));
		prayerTimeInSeconds[4] = TimeHelper.getSec(
				TimeHelper.to24(prayerTimes.get(4)),
				TimeHelper.getMinute(prayerTimes.get(4)),
				TimeHelper.getSecond(prayerTimes.get(4)));
		// sort ascending
		Arrays.sort(prayerTimeInSeconds);
		// default value is the first prayer in the day
		int nearestPrayer = prayerTimeInSeconds[0];
		// convert current time to seconds
		int currentTime = hour * 3600 + min * 60 + sec;

		for (Integer prayertime : prayerTimeInSeconds) {
			int pt = prayertime;
			if (pt > currentTime)// return first prayer after this time (
									// nearest prayer)
				return pt;
		}
		return nearestPrayer;
	}

	// -----------set method-----------//
	public void setSetting(settingAttributes sa) {
		azanAttribute aA = databaseHelper.getData(sa.city.cityNo);
		sa.city.latitude = aA.latitude;
		sa.city.longitude = aA.longitude;
		sa.city.timeZone = aA.timeZone;
		sa.country.countryNo = Integer.parseInt(aA.countryNo);
		this.xmlWriter(sa);
	}

	// -----------XML methods-----------//
	public void xmlWriter(settingAttributes sa) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this.context);
		Editor editor = pref.edit();
		editor.putString("country", Integer.toString(sa.country.countryNo));
		editor.putString("city", Integer.toString(sa.city.cityNo));
		editor.putString("latitude", sa.city.latitude);
		editor.putString("longitude", sa.city.longitude);
		editor.putString("timeZone", sa.city.timeZone);
		editor.putString("isCityChanged", "true");
		editor.commit();
	}

	public settingAttributes xmlReader() {
		settingAttributes sa = new settingAttributes();
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this.context);
		// Mecca values
		sa.city.timeZone = pref.getString("timeZone", "3");
		sa.city.latitude = pref.getString("latitude", "21.43");
		sa.city.longitude = pref.getString("longitude", "39.82");
		sa.calender = pref.getString("calendar", "UmmAlQuraUniv");
		sa.mazhab = pref.getString("mazhab", "Default");
		sa.season = pref.getString("season", "Winter");
		return sa;
	}

	

	public ArrayList<String> getPrayerTimes(int dd, int mm, int yy)
			throws IOException {

		ArrayList<String> prayerList = new ArrayList<String>();
		settingAttributes sa = this.xmlReader();
		PrayerTime prayerTime = new PrayerTime(
				Double.parseDouble(sa.city.longitude),
				Double.parseDouble(sa.city.latitude),
				Integer.parseInt(sa.city.timeZone), dd, mm, yy);
		prayerTime.setSeason(sa.season);
		prayerTime.setCalender(sa.calender);
		prayerTime.setMazhab(sa.mazhab);
		prayerTime.calculate();
		prayerList.add(prayerTime.fajrTime().text());
		prayerList.add(prayerTime.zuhrTime().text());
		prayerList.add(prayerTime.asrTime().text());
		prayerList.add(prayerTime.maghribTime().text());
		prayerList.add(prayerTime.ishaTime().text());
		return prayerList;
	}

	public Context getContext() {
		return context;
	}

	// find the current city based on its latitude and longtiude
	// I DON'T KNOW HOW THE METHOD WORKS !?
	public void findCurrentCity(double latitude, double longitude) {
		try {
			double min = 0;
			int i = 0, pos = 0;
			ArrayList<City> cityList = databaseHelper.getCityList(-1);
			for (City city : cityList) {
				double lat = Double.parseDouble(city.latitude);
				double lon = Double.parseDouble(city.longitude);
				double pk = (180 / 3.14159);
				double a1 = (lat / pk);
				double a2 = (lon / pk);

				double b1 = (latitude / pk);
				double b2 = (longitude / pk);

				double t1 = (Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math
						.cos(b2));
				double t2 = (Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math
						.sin(b2));
				double t3 = (Math.sin(a1) * Math.sin(b1));
				double tt = Math.acos(t1 + t2 + t3);
				double dist = (6366000 * tt);
				if (dist < min || i == 0) {
					min = dist;
					pos = i;
				}
				i++;
			}
			if (pos < cityList.size() && cityList.get(pos) != null) {
				settingAttributes sa = new settingAttributes();
				String cityId = (String) Integer
						.toString(cityList.get(pos).cityNo);
				sa.city.cityNo = -1;
				if (cityId != null) {
					sa.city.cityNo = Integer.parseInt(cityId);
				}
				if (sa.city.cityNo == -1)
					sa.city.cityNo = 1;
				this.setSetting(sa);
			}
		} catch (Exception e) {
		}
	}
}