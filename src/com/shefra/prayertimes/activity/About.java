package com.shefra.prayertimes.activity;

import com.shefra.prayertimes.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class About extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about);//TODO create a new layout
		/*Button close = (Button ) findViewById(R.id.about_close);
		close.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{
				finish();
			}
		});*/
		
	}

}
