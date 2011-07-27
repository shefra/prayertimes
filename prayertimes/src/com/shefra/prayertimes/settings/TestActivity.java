package com.shefra.prayertimes.settings;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.azanAttribute;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class TestActivity extends Activity {


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.test);
		TextView tv = (TextView) findViewById(R.id.city);
		try {

			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(this);

			if (pref != null) {
				//String city = pref.getString("city", "1"); // 1 is default value
															// .. ignore it
				Manager ma = new Manager(getApplicationContext());				
				ma.createDatabase();				
				azanAttribute aA = ma.getData(8064);
				if(aA != null){
				tv.setText(aA.cityName);
				}
			}
		} catch (Exception e) {

			tv.setText(e.toString() + ",");
		}

	}
	
	

}
