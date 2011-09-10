package com.shefra.prayertimes.settings;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.azanAttribute;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity {
	private MediaPlayer mPlayer ;
	// Called when the activity is first created. 

@Override

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.alert);
		mPlayer.start();
         
 
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
		/*
		Button b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mPlayer = MediaPlayer.create(TestActivity.this, R.raw.yassir);
				if(!(mPlayer.isPlaying()))
				mPlayer.start();
				
			}
		});
		*/
		Button b2 = (Button) findViewById(R.id.button2);
		b2.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mPlayer.stop();
				onDestroy();
			}
		});

	}
	
	@Override
	public void onDestroy(){
		this.mPlayer.stop();
		super.onDestroy();
	}
	
	

}
