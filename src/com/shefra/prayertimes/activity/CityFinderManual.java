package com.shefra.prayertimes.activity;

import com.shefra.prayertimes.R;
import com.shefra.prayertimes.R.id;
import com.shefra.prayertimes.R.layout;
import com.shefra.prayertimes.helper.DatabaseHelper;
import com.shefra.prayertimes.helper.Typefaces;
import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.Preference;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CityFinderManual extends Activity {

	private Spinner countrySpinner;
	private Spinner citySpinner;
	private Button saveButton;
	private View caneclButton;
	private boolean onCreateDone;
	private Preference preference;
	private DatabaseHelper databaseHelper;
	private boolean isCityCursorOpen;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			this.setContentView(R.layout.cityfindermanual);

			TextView textView = (TextView) findViewById(R.id.textView1);
			textView.setTypeface(Typefaces.get(this.getBaseContext(),
					"fonts/DroidNaskh-Regular.ttf"));

			citySpinner = (Spinner) findViewById(R.id.citySpinner);
			countrySpinner = (Spinner) findViewById(R.id.countrySpinner);

			databaseHelper = new DatabaseHelper(getApplicationContext());
			Manager manager = new Manager(this);
			preference = manager.getPreference();
			preference.fetchCurrentPreferences();

			this.fillCountrySpinner();
			countrySpinner.setSelection(Integer
					.valueOf(preference.city.country.id) - 1);
			this.fillCitySpinner(Long.parseLong(preference.city.country.id));

			countrySpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						public void onItemSelected(AdapterView<?> parentView,
								View selectedItemView, int position, long id) {
							// don't fill the Spinner if onCreate method is
							// working
							// to prevent double filling , since it has been
							// filled
							fillCitySpinner(id);
						}

						public void onNothingSelected(AdapterView<?> parentView) {
							// your code here
						}

					});

			this.saveButton = (Button) findViewById(R.id.saveButton);
			this.saveButton.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					try {
						long id = citySpinner.getSelectedItemId();
						City city = databaseHelper.getCity(id);
						Manager manager = new Manager(CityFinderManual.this);
						manager.updateCity(city, CityFinderManual.this);
						Toast.makeText(
								CityFinderManual.this,
								CityFinderManual.this
										.getString(R.string.cityUpdated),
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(CityFinderManual.this,
								MainActivity.class);
						startActivity(intent);
					} catch (Exception e) {
						//Log.e("tomaanina", e.getMessage(), e.getCause());
					}
				}

			});
		} catch (Exception e) {
			//Log.e("tomaanina", e.getMessage(), e.getCause());
		}
	}

	public void onStart() {
		super.onStart();
	}

	private int getItemPosition(Spinner spinner, String name) {
		int i = 0;
		SpinnerAdapter adapter = spinner.getAdapter();
		for (i = 0; i < adapter.getCount(); i++) {
			Cursor temp = (Cursor) adapter.getItem(i);
			String n = temp.getString(temp.getColumnIndex("cityName"));
			if (name.equals(n)) {
				return i;
			}
		}
		return i;
	}

	private void fillCountrySpinner() {

		Cursor cursor = databaseHelper.getCountryList();

		String[] columns = new String[] { "country_name" };
		int[] to = new int[] { android.R.id.text1 };

		SimpleCursorAdapter countryAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, cursor, columns, to);
		countryAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		this.countrySpinner.setAdapter(countryAdapter);

	}

	private void fillCitySpinner(long id) {
		if (this.isCityCursorOpen) {
			SimpleCursorAdapter b = (SimpleCursorAdapter) this.citySpinner
					.getAdapter();
			Cursor oldCursor = b.getCursor();

			if (oldCursor.isClosed() == false) {
				oldCursor.close();
			}
		}

		Cursor cursor = databaseHelper.getCityCursor(id);

		String[] columns = new String[] { "cityName" };
		int[] to = new int[] { android.R.id.text1 };

		SimpleCursorAdapter cityAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, cursor, columns, to);
		cityAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		this.citySpinner.setAdapter(cityAdapter);
		this.isCityCursorOpen =  true;

	}

	public void onStop() {
		super.onStop();

	}

	public void onDestroy() {
		super.onDestroy();
		SimpleCursorAdapter a = (SimpleCursorAdapter) this.countrySpinner
				.getAdapter();
		a.getCursor().close();
		SimpleCursorAdapter b = (SimpleCursorAdapter) this.citySpinner
				.getAdapter();
		b.getCursor().close();

		databaseHelper.close();

	}

}
