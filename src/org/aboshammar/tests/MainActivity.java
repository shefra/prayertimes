package org.aboshammar.tests;

import android.app.*;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import android.widget.*;

/*
EDIT EDIT EDIT */


/**
 * This activity allows you to have multiple views (in this case two {@link ListView}s)
 * in one tab activity.  The advantages over separate activities is that you can
 * maintain tab state much easier and you don't have to constantly re-create each tab
 * activity when the tab is selected.
 */
public class MainActivity extends TabActivity  {


	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main_activity);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, MainTab.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("pray");
	    spec.setIndicator("Main",res.getDrawable(R.drawable.pray_tab));
	    spec.setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, SettingTab.class);

	    spec = tabHost.newTabSpec("sett");
	    spec.setIndicator("Settings",res.getDrawable(R.drawable.pray_tab));
	    spec.setContent(intent);
	    tabHost.addTab(spec);
    

	    tabHost.setCurrentTab(0);
	}
	
	

    
}