package com.shefra.prayertimes.activity;


import com.shefra.prayertimes.R;
import com.shefra.prayertimes.R.id;
import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.CityLocationListener;
import com.shefra.prayertimes.manager.Manager;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CityFinderDatabase extends Activity {

	private TextView resultTextView;
	private Button notCorrectButton;
	private Button correctButton;
	private Button findButton;
	public View progressDialog;
	private CityLocationListener cityLoc;
	private com.shefra.prayertimes.activity.CityFinderDatabase.CityFinderTask cityFinderTask;
	public City city;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.cityfinderdatabase);

	    resultTextView = (TextView) findViewById(R.id.resultTextView);
	    notCorrectButton = (Button) findViewById(R.id.notCorrectButton);
	    correctButton = (Button) findViewById(R.id.correctButton);
	    findButton = (Button) findViewById(R.id.findCity);
		progressDialog = (ProgressBar) findViewById(R.id.progressBar1);
		progressDialog.setVisibility(View.GONE);
 
	    findButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					cityLoc = new CityLocationListener(CityFinderDatabase.this,2);
					cityLoc.startSearch();
					progressDialog.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e = e;
				}
			}
			
		});
	    
	    correctButton.setOnClickListener(new OnClickListener(){
	    	
	    	public void onClick(View v){
				Manager manager = new Manager(CityFinderDatabase.this);
				manager.updateCity(city,CityFinderDatabase.this);
				Intent intent = new Intent(CityFinderDatabase.this,MainActivity.class);
				startActivity(intent);
	    	}
	    });
	    

	    notCorrectButton.setOnClickListener(new OnClickListener(){
	    	
	    	public void onClick(View v){
				Intent intent = new Intent(CityFinderDatabase.this,MainActivity.class);
				startActivity(intent);
	    	}
	    });
	}
	
	public void stopSearch(Location location) {
		// if (cityFinderTask != null) {
		// is it safe to change the done var from UI thread?
		// in another way: is it necessary to do that manually ?
		// cityFinderTask.setDone(true);

		// cityFinderTask.cancel(true);

		// }
		cityFinderTask = new CityFinderTask();
		this.cityFinderTask.execute(new Location[] { location });

	}

	private class CityFinderTask extends AsyncTask<Location, String, String> {
		private boolean done;
		private Location loc;
		@Override
		protected void onPreExecute() {
			try {
				super.onPreExecute();
			} catch (Exception e) {
				Log.e("", e.getMessage(), e.getCause());
			}
		}

		@Override
		protected String doInBackground(Location... params) {
			try {
				// TODO: wait 10 seconds then stop the process and invoke
				Location location = params[0];
				this.loc = location;
			} catch (Exception e) {
				Log.e("", e.getMessage(), e.getCause());
			}
			return null;
		}

		

		protected void onPostExecute(String result) {
			progressDialog.setVisibility(View.GONE);
			City city = null;
			try {
				
				Manager manager = new Manager(CityFinderDatabase.this);
				
				city = manager.findCurrentCity(loc.getLongitude(), loc.getLatitude());
					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultTextView.setText("Done:" + city.name + " , "
					+ loc.getLongitude() + " , " + loc.getLatitude());
			CityFinderDatabase.this.city = city;
		}


	}

}
