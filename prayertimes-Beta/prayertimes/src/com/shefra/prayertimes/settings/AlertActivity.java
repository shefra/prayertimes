/*
 * AlertActivity Class : implements OnCompletionListener beside extending Activity
 * because we need to finish() the activity once the Sound is completed
 *   
 */
package com.shefra.prayertimes.settings;

import com.AzizHuss.ArabicRehaper.ArabicReshape;
import com.shefra.prayertimes.*;
import android.app.Activity;
import android.graphics.Typeface;
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
		Typeface droidBold = Typeface.createFromAsset(getAssets(),"fonts/DroidSans-Bold.ttf");
		 
		String ardroid = ArabicReshape.reshape("Ø§Ù„Ù„Ù‡Ù… Ø±Ø¨ Ù‡Ø°Ù‡ Ø§Ù„Ø¯Ø¹ÙˆØ© Ø§Ù„ØªØ§Ù…Ù‘ÙŽØ©ØŒ ÙˆØ§Ù„ØµÙ„Ø§Ø© Ø§Ù„Ù‚Ø§Ø¦Ù…Ø©ØŒ Ø¢Øª Ù…Ø­Ù…Ø¯Ø§Ù‹ Ø§Ù„ÙˆØ³ÙŠÙ„Ø© ÙˆØ§Ù„Ù�Ø¶ÙŠÙ„Ø©ØŒ ÙˆØ§Ø¨Ø¹Ø«Ù‡ Ø§Ù„Ù„Ù‡Ù… Ù…Ù‚Ø§Ù…Ø§Ù‹ Ù…Ø­Ù…ÙˆØ¯Ø§Ù‹ Ø§Ù„Ø°ÙŠ ÙˆØ¹Ø¯ØªÙ‡.");
		
		TextView azanDoaa = (TextView) findViewById(R.id.azandoaa);
		azanDoaa.setText(ardroid);
		azanDoaa.setTypeface(droidBold);
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




