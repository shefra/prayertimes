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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import com.shefra.prayertimes.R;
import com.shefra.prayertimes.activity.AlertActivity;
import com.shefra.prayertimes.activity.MainActivity;
import com.shefra.prayertimes.helper.DatabaseHelper;
import com.shefra.prayertimes.helper.TimeHelper;
import com.shefra.prayertimes.moazen.PrayerTime;
import com.shefra.prayertimes.services.PrayerReceiver;
import com.shefra.prayertimes.services.PrayerService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.*;
import android.media.AudioManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;

// Manager is the main class that works as layer  between the app and database/xml files
public class Manager {

	private Context context;
	DatabaseHelper databaseHelper;
	private static Intent prayerIntet;
	private static PendingIntent prayerPendingIntent;
	private static AlarmManager prayerAlarmManager;
	public static long interval;
	private static PrayerState prayerState;
	private static Service prayerService;
	private static int UNIQUE_ID = 32289;
	public static boolean isPhoneIdle = true;

	public Manager(Context applicationContext) {

		this.context = applicationContext;
		databaseHelper = new DatabaseHelper(applicationContext);
	}

	public static void acquireScreen(Context context) {
		PowerManager pm = (PowerManager) context.getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = pm
				.newWakeLock(
						(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
								| PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP),
						"TAG");
		wakeLock.acquire();
	}

	public static void releaseScreen(Context context) {
		KeyguardManager keyguardManager = (KeyguardManager) context
				.getApplicationContext().getSystemService(
						Context.KEYGUARD_SERVICE);
		KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
		keyguardLock.disableKeyguard();
	}

	public static void initPrayerAlarm(Service service,
			Class<PrayerReceiver> receiver) {
		Manager.prayerService = service; // we may need it ?
		Manager.prayerIntet = new Intent(service, receiver);
		Manager.prayerPendingIntent = PendingIntent
				.getBroadcast(service, 1234432, Manager.prayerIntet,
						PendingIntent.FLAG_UPDATE_CURRENT);
		Manager.prayerAlarmManager = (AlarmManager) service
				.getSystemService(Context.ALARM_SERVICE);
		Manager.prayerAlarmManager.set(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 1000, Manager.prayerPendingIntent);
	}

	public static void updatePrayerAlarm(long newTimeInterval) {
		Manager.prayerAlarmManager.set(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + newTimeInterval,
				Manager.prayerPendingIntent);
	}

	public static void cancelPrayerAlarm() {
		Manager.prayerAlarmManager.cancel(prayerPendingIntent);
	}

	public static void initPrayerState(Context context) {
		Manager.prayerState = new PrayerState(context);

	}

	public static PrayerState getPrayerState() {
		return prayerState;
	}

	// get nearest prayer time based on current time

	public static int computeNearestPrayerTime(Context context, int hour,
			int min, int sec, int year, int month, int day) throws IOException {
		ArrayList<String> prayerTimes = getPrayerTimes(context, day, month,
				year);
		int[] prayerTimeInSeconds = new int[5];
		prayerTimes.get(0);
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
			if (pt >= currentTime)// return first prayer after this time (
									// nearest prayer)
				return pt;
		}
		return nearestPrayer;
	}

	/*
	 * public static int computePreviuosPrayerTime(Context context, int hour,
	 * int min, int sec, int year, int month, int day) throws IOException {
	 * 
	 * ArrayList<String> prayerTimes = getPrayerTimes(context, day, month,
	 * year); Integer[] prayerTimeInSeconds = new Integer[5];
	 * 
	 * // Convert prayer times to seconds prayerTimeInSeconds[0] = new
	 * Integer(TimeHelper.getSec( TimeHelper.to24(prayerTimes.get(0)),
	 * TimeHelper.getMinute(prayerTimes.get(0)),
	 * TimeHelper.getSecond(prayerTimes.get(0)))); prayerTimeInSeconds[1] = new
	 * Integer(TimeHelper.getSec( TimeHelper.to24(prayerTimes.get(1)),
	 * TimeHelper.getMinute(prayerTimes.get(1)),
	 * TimeHelper.getSecond(prayerTimes.get(1)))); prayerTimeInSeconds[2] = new
	 * Integer(TimeHelper.getSec( TimeHelper.to24(prayerTimes.get(2)),
	 * TimeHelper.getMinute(prayerTimes.get(2)),
	 * TimeHelper.getSecond(prayerTimes.get(2)))); prayerTimeInSeconds[3] = new
	 * Integer(TimeHelper.getSec( TimeHelper.to24(prayerTimes.get(3)),
	 * TimeHelper.getMinute(prayerTimes.get(3)),
	 * TimeHelper.getSecond(prayerTimes.get(3)))); prayerTimeInSeconds[4] = new
	 * Integer(TimeHelper.getSec( TimeHelper.to24(prayerTimes.get(4)),
	 * TimeHelper.getMinute(prayerTimes.get(4)),
	 * TimeHelper.getSecond(prayerTimes.get(4))));
	 * 
	 * // sort descending Arrays.sort(prayerTimeInSeconds, new
	 * Comparator<Integer>() {
	 * 
	 * @Override public int compare(Integer lhs, Integer rhs) { return
	 * rhs.compareTo(lhs); } });
	 * 
	 * // default value is the last prayer in the day ( Witch is Isha) //
	 * remember , we sorted it descending int previousTime =
	 * prayerTimeInSeconds[0]; int firstTime = prayerTimeInSeconds[4]; //
	 * convert current time to seconds int currentTime = hour * 3600 + min * 60
	 * + sec; int i=0; for (Integer prayertime : prayerTimeInSeconds) { int pt =
	 * prayertime; i++; // return the last prayer if (pt <= currentTime) return
	 * pt; } // in case if the current time is less then all the prayers time
	 * 
	 * if(i == 5) return firstTime; else return previousTime;
	 * 
	 * }
	 */

	// -----------set method-----------//
	/*
	 * public void setSetting(SettingAttributes sa) { azanAttribute aA =
	 * databaseHelper.getData(sa.city.cityNo); sa.city.latitude = aA.latitude;
	 * sa.city.longitude = aA.longitude; sa.city.timeZone = aA.timeZone;
	 * sa.country.countryNo = Integer.parseInt(aA.countryNo);
	 * this.setSettingAttributes(sa); }
	 * 
	 * // -----------XML methods-----------// public void
	 * setSettingAttributes(SettingAttributes sa) { SharedPreferences pref =
	 * PreferenceManager .getDefaultSharedPreferences(this.context); Editor
	 * editor = pref.edit(); editor.putString("country",
	 * Integer.toString(sa.country.countryNo)); editor.putString("city",
	 * Integer.toString(sa.city.cityNo)); editor.putString("latitude",
	 * sa.city.latitude); editor.putString("longitude", sa.city.longitude);
	 * editor.putString("timeZone", sa.city.timeZone);
	 * editor.putString("isCityChanged", "true"); editor.commit(); }
	 * 
	 * public static SettingAttributes getSettingAttributes(Context context) {
	 * SettingAttributes sa = new SettingAttributes(); SharedPreferences pref =
	 * PreferenceManager .getDefaultSharedPreferences(context); // Mecca values
	 * sa.city.timeZone = pref.getString("timeZone", "3"); sa.city.latitude =
	 * pref.getString("latitude", "21.43"); sa.city.longitude =
	 * pref.getString("longitude", "39.82"); sa.calender =
	 * pref.getString("calendar", "UmmAlQuraUniv"); sa.mazhab =
	 * pref.getString("mazhab", "Default"); sa.season = pref.getString("season",
	 * "Winter"); return sa; }
	 */

	public static ArrayList<String> getPrayerTimes(Context context, int dd,
			int mm, int yy) throws IOException {

		ArrayList<String> prayerList = new ArrayList<String>();
		Manager manager = new Manager(context);
		Preference preference = manager.getPreference();
		preference.fetchCurrentPreferences();
		PrayerTime prayerTime = new PrayerTime(
				Double.parseDouble(preference.city.longitude),
				Double.parseDouble(preference.city.latitude),
				(int) preference.city.timeZone, dd, mm, yy);
		prayerTime.setSeason(preference.season);
		prayerTime.setCalender(preference.calender);
		prayerTime.setMazhab(preference.mazhab);
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
	public City findCurrentCity(double latitude, double longitude) {
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
				String cityId = cityList.get(pos).id;
				Integer cityNo = -1;
				if (cityId != null) {
					cityNo = Integer.parseInt(cityId);
				}
				if (cityNo == -1)
					cityNo = 1;

				City city = databaseHelper.getCity(cityNo);
				databaseHelper.close();
				return city;

			}

		} catch (Exception e) {
		} finally {
			databaseHelper.close();
		}
		return null;

	}

	public static void playAzanNotification(Context context) {
		Intent intent;
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String azanMode = pref.getString("notSound", "short");
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (azanMode.equals("full")
				&& am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL
				&& Manager.isPhoneIdle == true) {
			intent = new Intent(context, AlertActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra("runFromService", true);
			context.startActivity(intent);
		} else if (!(azanMode.equals("disable"))
				&& (azanMode.equals("short") || (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT || am
						.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE))
				|| Manager.isPhoneIdle == false) {

			CharSequence contentTitle = context.getString(R.string.notTitle);
			CharSequence contentText = context.getString(R.string.notContent);
			long when = System.currentTimeMillis();

			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent notificationIntent = new Intent(context, MainActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);

			Notification notification = new Notification(
					com.shefra.prayertimes.R.drawable.icon, contentText, when);
			notification.sound = Uri
					.parse("android.resource://com.shefra.prayertimes/raw/notification");
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			mNotificationManager.notify(UNIQUE_ID, notification);
		}

	}

	public Preference getPreference() {

		return new Preference(this.context);
	}

	public void updateCity(City city, Activity activity) {
		Preference pref = this.getPreference();
		pref.setCityName(city.name);
		pref.setCityNo(city.id);
		pref.setCountryName(city.country.name);
		pref.setCountryNo(city.country.id);
		pref.setLongitude(city.longitude);
		pref.setLatitude(city.latitude);
		pref.setTimeZone(city.timeZone);

    	Manager.cancelPrayerAlarm();
    	Manager.initPrayerState(Manager.prayerService);
		Manager.initPrayerAlarm(Manager.prayerService,PrayerReceiver.class);
    	
	}
	
	// it does not work ? 
	public void restartPrayerService(Activity activty) {
		Intent intent = new Intent(activty, PrayerService.class);
		context.startService(intent);
	}

}