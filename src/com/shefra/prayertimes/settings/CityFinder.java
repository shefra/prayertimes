package com.shefra.prayertimes.settings;

import com.shefra.prayertimes.MainActivity;
import com.shefra.prayertimes.R;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.SettingAttributes;
import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class CityFinder extends Activity {

	private Button findCityButton;
	ProgressBar progressDialog;
	// private CityFinderTask cityFinderTask;
	private TextView textViewResult;
	private CityLocationListener cityLoc;
	private CityFinderTask cityFinderTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cityfinder);
		progressDialog = (ProgressBar) findViewById(R.id.progressBar1);
		progressDialog.setVisibility(View.GONE);
		findCityButton = (Button) findViewById(R.id.findCity);
		textViewResult = (TextView) findViewById(R.id.textViewResult);

		findCityButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					cityLoc = new CityLocationListener(CityFinder.this);
					cityLoc.startSearch();
					progressDialog.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e = e;
				}
			}
		});

	}

	private class CityFinderTask extends AsyncTask<Location, String, String> {
		private boolean done;

		@Override
		protected void onPreExecute() {
			try {
				//done = false;
				super.onPreExecute();
				//cityLoc = new CityLocationListener(CityFinder.this);
				//cityLoc.startSearch();
				//progressDialog.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Log.e("", e.getMessage(), e.getCause());
			}
		}

		@Override
		protected String doInBackground(Location... params) {
			try {
				// TODO: wait 10 seconds then stop the process and invoke
				// LocationListener stopper.
				// cityLoc.stopSearch();
				Location location = params[0];
				Manager manager = new Manager(CityFinder.this);
				manager.findCurrentCity(location.getLatitude(),
						location.getLongitude());

			} catch (Exception e) {
				Log.e("", e.getMessage(), e.getCause());
			}
			return null;
		}

		protected void onPostExecute(String result) {
			progressDialog.setVisibility(View.GONE);
			Manager manager = new Manager(CityFinder.this);
			SettingAttributes att = Manager.getSettingAttributes(CityFinder.this);
			textViewResult.setText("Done:" + att.city.cityName + " , "
					+ att.city.longitude + " , " + att.city.latitude);
		}

	}

	public void stopSearch(Location location) {
		// if (cityFinderTask != null) {
		// is it safe to change the done var from UI thread?
		// in another way: is it necessary to do that manually ?
		// cityFinderTask.setDone(true);

		// cityFinderTask.cancel(true);

		// }
		cityFinderTask = new CityFinderTask();
		CityFinder.this.cityFinderTask.execute(new Location[] {location});

		

	}

}
