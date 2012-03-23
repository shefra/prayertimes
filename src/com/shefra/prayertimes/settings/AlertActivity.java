/*
 * AlertActivity Class : implements OnCompletionListener beside extending Activity
 * because we need to finish() the activity once the Sound is completed
 *   
 */
package com.shefra.prayertimes.settings;


//import com.AzizHuss.ArabicRehaper.ArabicReshape;
import com.shefra.prayertimes.*;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
 
public class AlertActivity extends Activity implements OnCompletionListener {
	private MediaPlayer mPlayer;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.alert);
		 
		String ardroid = getString(R.string.azandoaa);
		
		TextView azanDoaa = (TextView) findViewById(R.id.azandoaa);
		azanDoaa.setText(ardroid);
		
		try {
			
			mPlayer = MediaPlayer.create(AlertActivity.this, R.raw.majed); 
			mPlayer.start();
			mPlayer.setOnCompletionListener(AlertActivity.this);
		
		} catch (Exception e) {

		}
		
		Button b = (Button) findViewById(R.id.button2);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mPlayer.stop();
				finish();
			}
		});
  
	}

	@Override
	public void onDestroy(){
		this.mPlayer.stop();
		super.onDestroy();
	}
	
	public void onCompletion(MediaPlayer mp) {
		finish();
	}
	
}




