package com.shefra.prayertimes;

import com.shefra.prayertimes.settings.SettingsActivity;
import com.shefra.prayertimes.settings.TestActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        
       // setContentView(R.layout.main);
 
        Button settB = new Button(this);
		settB.setText("Settings");
		settB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Toast.makeText(TestActivity.this, "ImageButton clicked!",
				// Toast.LENGTH_SHORT).show();
				Intent myIntent = new Intent(v.getContext(), SettingsActivity.class);
				startActivity(myIntent);
			}
		});
		
		Button testB = new Button(this);
		testB.setText("Test Settings");
		testB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Toast.makeText(TestActivity.this, "ImageButton clicked!",
				// Toast.LENGTH_SHORT).show();
				Intent myIntent = new Intent(v.getContext(), TestActivity.class);
				startActivity(myIntent);
			}
		});
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		ll.setGravity(Gravity.CENTER);
		ll.addView(settB);
		ll.addView(testB);
		this.setContentView(ll);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    menu.add(0, 1, 1, "Settings");
    menu.add(0, 2, 2, "Test");
    menu.add(0, 3, 3, "About");
    return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
     
     switch(item.getItemId())
     {
     case 1:
    		Intent myIntent = new Intent(this, SettingsActivity.class);
			startActivity(myIntent);
      return true;
     case 2:
 		Intent myIntent2 = new Intent(this, TestActivity.class);
		startActivity(myIntent2);

      return true;
     case 3:
      
      return true;
      
       
     }
     return super.onOptionsItemSelected(item);
    
    }
}