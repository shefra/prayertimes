package org.aboshammar.tests;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

public class SettingTab extends Activity {
    public void onCreate(Bundle savedInstanceState) {
    	List<String> model = new ArrayList<String>();
    	
    	model.add("Riyadh)");
    	model.add("Jeddah)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sett_tab);
        Spinner s = (Spinner) findViewById(R.id.spinner);
       
        
      /*  ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, model);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(spinnerArrayAdapter);*/


        
    }

}