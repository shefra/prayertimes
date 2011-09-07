package com.shefra.prayertimes.settings;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.azanAttribute;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
 
public class AlertActivity extends Activity implements OnCompletionListener {
	private static final String TAG = "AlertPlayer";
	private MediaPlayer mPlayer ;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.alert);//TODO create a new layout
		try {
	
			SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(this);
			
			mPlayer = MediaPlayer.create(AlertActivity.this, R.raw.majed);//TODO it should be Dynamically 
			mPlayer.start();
		
			mPlayer.setOnCompletionListener(AlertActivity.this);

		TextView tv = (TextView) findViewById(R.id.city);
		
			if (pref != null) {
				String city = pref.getString("city", "1"); // 1 is default value
															// .. ignore it
				/*Manager ma = new Manager(getApplicationContext());				
				ma.createDatabase();				
				azanAttribute aA = ma.getData(8064);*/
				//if(aA != null){
				//tv.setText(city);
				//}
			}
		} catch (Exception e) {

			//tv.setText(e.toString() + ",");
		}
		
		Button b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
			}
		});
		
		Button b2 = (Button) findViewById(R.id.button2);
		b2.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mPlayer.stop();
				finish();
				
			}
		});
  
	}
	/*public void onCompletionListener(){
		Log.d(TAG, "onCompletionListener called");
		finish();
	}*/
	@Override
	public void onDestroy(){
		Log.d(TAG, "onDestroy called");
		this.mPlayer.stop();
		super.onDestroy();
	}
	public void onCompletion(MediaPlayer mp) {
		Log.d(TAG, "onCompletion called");
		finish();
		
	}
	
	

}




