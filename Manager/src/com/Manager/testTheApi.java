package com.Manager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class testTheApi extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Manager ma = ((Manager)getApplicationContext());
        azanAttribute aA = ma.getData(8065);
        TextView tv = new TextView(this);
        tv.setText(aA.cityName);
        setContentView(tv);

	}
}
