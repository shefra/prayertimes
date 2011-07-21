package com.shefra.prayertimes;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.shefra.prayertimes.services.ServiceSetAlarm;
import com.shefra.prayertimes.settings.SettingsActivity;
import com.shefra.prayertimes.settings.TestActivity;
import com.shefra.prayertimes.manager.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// run service
		Intent intent = new Intent(this, ServiceSetAlarm.class);
		startService(intent);

		this.setContentView(R.layout.main);
		// setContentView(R.layout.main);

		Manager manager = new Manager(getApplicationContext());
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		int dd = date.getDate();//calendar.get(Calendar.DAY_OF_MONTH);
		int mm = date.getMonth()+1;//7;//calendar.get(Calendar.MONTH+1);
		int yy = date.getYear()+1900;//calendar.get(Calendar.YEAR);
		int h = date.getHours();//calendar.get(Calendar.HOUR_OF_DAY);
		int m = date.getMinutes();//calendar.get(Calendar.MINUTE);
		int s = date.getSeconds();//calendar.get(Calendar.SECOND);

		try {
			List<String> prayersList = manager.getPrayerTimes(dd, mm, yy);
			TextView fajrTime = (TextView) findViewById(R.id.fajrTime);
			TextView duhrTime = (TextView) findViewById(R.id.duhrTime);
			TextView asrTime = (TextView) findViewById(R.id.asrTime);
			TextView magribTime = (TextView) findViewById(R.id.magribTime);
			TextView ishaTime = (TextView) findViewById(R.id.ishaTime);
			fajrTime.setText(prayersList.get(0));
			duhrTime.setText(prayersList.get(1));
			asrTime.setText(prayersList.get(2));
			magribTime.setText(prayersList.get(3));
			ishaTime.setText(prayersList.get(4));

			TextView remainingTime = (TextView) findViewById(R.id.remainingTime);
			int time = manager.nearestPrayerTime(h, m,s, yy, mm, dd);
			int def =  manager.diffrent((h*3600+m*60+s),time);
			remainingTime.setText(Manager.secondsToTime(def));			/*
			 * TimerTask task = new RemainingTime(remainingTime); new
			 * Timer().schedule(task, 1,1000);
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "Settings");
		menu.add(0, 2, 2, "Test");
		menu.add(0, 3, 3, "About");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			Intent myIntent = new Intent(this, SettingsActivity.class);
			startActivity(myIntent);
			return true;
		case 2:
			Intent myIntent2 = new Intent(this, TestActivity.class);
			startActivity(myIntent2);

			return true;
		case 3:

			return true;

		}
		return super.onOptionsItemSelected(item);

	}

	public void updateRemainingTime() {

	}
}