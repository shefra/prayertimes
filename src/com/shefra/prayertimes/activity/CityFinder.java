package com.shefra.prayertimes.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.shefra.prayertimes.R;
import com.shefra.prayertimes.R.id;
import com.shefra.prayertimes.helper.Typefaces;
import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.CityLocationListener;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.Preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CityFinder extends Activity {

	protected static final int SEARCH_TYPE_DATABASE = 1;
	protected static final int SEARCH_TYPE_INTERNET = 2;
	public static final int SEARCH_TIME = 1000 * 120;
	private CityLocationListener cityLoc;
	private com.shefra.prayertimes.activity.CityFinder.CityFinderTask cityFinderTask;
	public City city;
	protected int searchType;
	int TIME_ZONE = 0, CITY_DATA = 1;
	String[] url = new String[2];
	String[] data = new String[2];

	private TextView resultTextView;
	public View progressDialog;

	private Button findCityNoInternet;
	private Button findCityUsingInternet;
	private Button noSearchButton;
	private Button homeButton;
	private Button researchButton;
	private Button correctButton;
	private TextView descTextView;
	private Timer myTimer;
	public boolean searchDBError;
	public boolean searchInternetError;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cityfinder);
		try {
			descTextView = (TextView) findViewById(R.id.dlgMsg);
			resultTextView = (TextView) findViewById(R.id.resultTextView);
			descTextView.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/DroidNaskh-Regular.ttf"));
			resultTextView.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/DroidNaskh-Regular.ttf"));
			
			findCityNoInternet = (Button) findViewById(R.id.findCityNoInternet);
			findCityUsingInternet = (Button) findViewById(R.id.findCityUsingInternet);
			noSearchButton = (Button) findViewById(R.id.nosearch);

			homeButton = (Button) findViewById(R.id.findCityHome);
			correctButton = (Button) findViewById(R.id.findCityCorrect);
			researchButton = (Button) findViewById(R.id.findCityResearcht);
			homeButton.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/DroidNaskh-Regular.ttf"));
			correctButton.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/DroidNaskh-Regular.ttf"));
			researchButton.setTypeface(Typefaces.get(this.getBaseContext(), "fonts/DroidNaskh-Regular.ttf"));
			
			progressDialog = (ProgressBar) findViewById(R.id.progressBar1);
			progressDialog.setVisibility(View.GONE);
			Manager m = new Manager(this);
			Preference pref = m.getPreference();
			pref.fetchCurrentPreferences();
			resultTextView.setText(pref.city.name);

			findCityNoInternet.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					try {
						CityFinder.this.searchType = CityFinder.SEARCH_TYPE_DATABASE;
						cityLoc = new CityLocationListener(CityFinder.this, 2);
						cityLoc.startSearch();
						progressDialog.setVisibility(View.VISIBLE);
						CityFinder.this.waitSearch(SEARCH_TIME);

					} catch (Exception e) {
						//Log.e("tomaanina", e.getMessage(), e.getCause());
					}
				}

			});

			findCityUsingInternet.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					try {
						CityFinder.this.searchType = CityFinder.SEARCH_TYPE_INTERNET;
						cityLoc = new CityLocationListener(CityFinder.this, 2);
						cityLoc.startSearch();
						progressDialog.setVisibility(View.VISIBLE);

						CityFinder.this.waitSearch(SEARCH_TIME);

					} catch (Exception e) {
						//Log.e("tomaanina", e.getMessage(), e.getCause());
					}
				}

			});

			researchButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					CityFinder.this.city = null;

					CityFinder.this.researchButton.setVisibility(View.GONE);
					CityFinder.this.homeButton.setVisibility(View.GONE);
					CityFinder.this.correctButton.setVisibility(View.GONE);
					CityFinder.this.descTextView.setText(CityFinder.this
							.getString(R.string.cityfinder_desc));

					CityFinder.this.findCityNoInternet
							.setVisibility(View.VISIBLE);
					CityFinder.this.findCityUsingInternet
							.setVisibility(View.VISIBLE);
					CityFinder.this.noSearchButton.setVisibility(View.VISIBLE);
				}
			});

			correctButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Manager manager = new Manager(CityFinder.this);
					manager.updateCity(city, CityFinder.this);
					Intent intent = new Intent(CityFinder.this,
							MainActivity.class);
					startActivity(intent);
				}
			});

			noSearchButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent intent = new Intent(CityFinder.this,
							MainActivity.class);
					startActivity(intent);

				}
			});

			homeButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent intent = new Intent(CityFinder.this,
							MainActivity.class);
					startActivity(intent);
				}
			});
		} catch (Exception e) {
			//Log.e("tomaanina", e.getMessage(), e.getCause());
		}
	}

	private void waitSearch(int ms) {
		myTimer = new Timer();
		TimerTask scanTask;
		final Handler handler = new Handler();

		scanTask = new TimerTask() {
			public void run() {

				handler.post(new Runnable() {
					public void run() {
						try {
							CityFinder.this.cityLoc.updateWithNewLocation(null);
						} catch (Exception e) {

						}
					}
				});
			}
		};

		myTimer.schedule(scanTask, ms);
	}

	public void onSearchStopped(Location location) {
		// if (cityFinderTask != null) {
		// is it safe to change the done var from UI thread?
		// in another way: is it necessary to do that manually ?
		// cityFinderTask.setDone(true);

		// cityFinderTask.cancel(true);

		// }
		this.myTimer.cancel();
		cityFinderTask = new CityFinderTask();
		this.cityFinderTask.execute(new Location[] { location });

	}

	private class CityFinderTask extends
			AsyncTask<Location, String, String> {
		private Location loc;

		@Override
		protected void onPreExecute() {
			try {
				super.onPreExecute();
			} catch (Exception e) {
				//Log.e("", e.getMessage(), e.getCause());
			}
		}

		@Override
		protected String doInBackground(Location... params) {
			try {
				// TODO: wait 10 seconds then stop the process and invoke
				Location location = params[0];
				this.loc = location;

				if (CityFinder.this.searchType == CityFinder.SEARCH_TYPE_DATABASE) {
					try {

						Manager manager = new Manager(CityFinder.this);

						city = manager.findCurrentCity(loc.getLatitude(),
								loc.getLongitude());
						CityFinder.this.searchDBError = false;

					} catch (Exception e) {
						CityFinder.this.searchDBError = true;
					}
				} else {
					try {
						city = this.getPosition(loc.getLongitude(),
								loc.getLatitude());
						CityFinder.this.searchInternetError = false;

					} catch (Exception e) {
						CityFinder.this.searchInternetError = true;

					}
				}
			} catch (Exception e) {
				//Log.e("", e.getMessage(), e.getCause());
			}
			return null;
		}

		protected void onPostExecute(String result) {
			progressDialog.setVisibility(View.GONE);
			if (this.loc == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						CityFinder.this);
				builder.setMessage(
						CityFinder.this
								.getString(R.string.gpsAndNetworkIsDisabled))
						.setPositiveButton(
								CityFinder.this.getString(R.string.reSearch),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								})
						.setNegativeButton(R.string.manualSearch,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										Intent intent = new Intent(
												CityFinder.this,
												CityFinderManual.class);
										CityFinder.this.startActivity(intent);
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
				return;
			}
			if (CityFinder.this.searchType == CityFinder.SEARCH_TYPE_DATABASE) {
				try {

					if (CityFinder.this.searchDBError) {
						throw new Exception();
					}

					resultTextView.setText(CityFinder.this.city.name);

					CityFinder.this.findCityNoInternet.setVisibility(View.GONE);
					CityFinder.this.findCityUsingInternet
							.setVisibility(View.GONE);
					CityFinder.this.noSearchButton.setVisibility(View.GONE);
					CityFinder.this.researchButton.setVisibility(View.VISIBLE);
					CityFinder.this.homeButton.setVisibility(View.VISIBLE);
					CityFinder.this.correctButton.setVisibility(View.VISIBLE);
					CityFinder.this.descTextView.setText(CityFinder.this
							.getString(R.string.cityfinder_desc2));
				} catch (Exception e) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CityFinder.this);
					builder.setMessage(
							CityFinder.this.getString(R.string.noCityInDB))
							.setPositiveButton(
									CityFinder.this.getString(R.string.close),
									null);
					AlertDialog alert = builder.create();
					alert.show();
					//Log.e("tomaanina", e.getMessage(), e.getCause());
				}
			} else {
				try {
					if (CityFinder.this.searchInternetError) {
						throw new Exception();
					}
					resultTextView.setText(CityFinder.this.city.name);
					CityFinder.this.findCityNoInternet.setVisibility(View.GONE);
					CityFinder.this.findCityUsingInternet
							.setVisibility(View.GONE);
					CityFinder.this.noSearchButton.setVisibility(View.GONE);
					CityFinder.this.descTextView.setText(CityFinder.this
							.getString(R.string.cityfinder_desc2));
					CityFinder.this.researchButton.setVisibility(View.VISIBLE);
					CityFinder.this.homeButton.setVisibility(View.VISIBLE);
					CityFinder.this.correctButton.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CityFinder.this);
					builder.setMessage(
							CityFinder.this.getString(R.string.noInternet))
							.setPositiveButton(
									CityFinder.this.getString(R.string.close),
									null);
					AlertDialog alert = builder.create();
					alert.show();
					//Log.e("tomaanina", e.getMessage(), e.getCause());
				}

			}

		}

		public City getPosition(double lon, double lat)
				throws ClientProtocolException, IOException,
				ParserConfigurationException, Exception {
			City city = new City();
			url[TIME_ZONE] = "http://www.earthtools.org/timezone/" + lat + "/"
					+ lon;
			url[CITY_DATA] = "http://173.194.67.95/maps/api/geocode/json?latlng="
					+ lat + "," + lon + "&sensor=true";
			data[TIME_ZONE] = getRequest(url[TIME_ZONE]);
			data[CITY_DATA] = getRequest(url[CITY_DATA]);
			city = getLocation(data[CITY_DATA]);
			city.timeZone = getTimeZone(data[TIME_ZONE]);
			city.latitude = Double.toString(lat);
			city.longitude = Double.toString(lon);

			return city;
		}

		public int getTimeZone(String TimeZoneContent)
				throws ParserConfigurationException, ClientProtocolException,
				Exception, SAXException {

			CharacterData cd;
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(TimeZoneContent));
				Document doc = db.parse(is);

				NodeList nodeList = doc.getElementsByTagName("offset");
				Element n = (Element) nodeList.item(0);
				String data = getCharacterDataFromElement(n);
				return Integer.valueOf(data);
			} catch (Exception e) {
				throw e;
			}

		}

		public City getLocation(String s) throws ClientProtocolException {
			City temp = new City();

			try {

				JSONObject jArray = new JSONObject(s);

				JSONArray jResult = jArray.getJSONArray("results");
				JSONObject jAddCom = jResult.getJSONObject(0);

				JSONArray jResult1 = jAddCom.getJSONArray("address_components");
				List<JSONObject> AddCombList = new ArrayList<JSONObject>();
				for (int i = 0; i < jResult1.length(); i++) {
					AddCombList.add(jResult1.getJSONObject(i));
					if (AddCombList.get(i).getString("types")
							.contains("country")) {
						temp.country.longName = AddCombList.get(i).getString(
								"long_name");
						temp.country.name = temp.country.longName;
						temp.country.shortName = AddCombList.get(i).getString(
								"short_name");
						if (temp.country.name == null) {
							temp.country.name = temp.country.shortName;
						}
					} else if (AddCombList.get(i).getString("types")
							.contains("administrative_area_level_1"))
						temp.name = AddCombList.get(i).getString("long_name");
				}

			} catch (JSONException e) {
				Toast msg = Toast.makeText(getApplicationContext(),
						"Error thrown: " + e.getMessage(), Toast.LENGTH_LONG);
				msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2,
						msg.getYOffset() / 2);
				msg.show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return temp;
		}

		public String getRequest(String url) {

			String line = "";
			String content = "";
			HttpGet httpRequest = new HttpGet(url);
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			int timeoutConnection = 10000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 10000;

			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient client = new DefaultHttpClient(httpParameters);

			HttpResponse httpResponse;
			try {
				httpResponse = client.execute(httpRequest);

				final int statusCode = httpResponse.getStatusLine()
						.getStatusCode();

				if (statusCode != HttpStatus.SC_OK) {
					//Log.e("tomaanina", "Time Out");
				}

				HttpEntity httpEntity = httpResponse.getEntity();
				// InputStream in = httpEntity.getContent();

				// return in;

				BufferedReader in = new BufferedReader(new InputStreamReader(
						httpEntity.getContent()));
				while ((line = in.readLine()) != null)
					content += line;
				in.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return content;
		}

	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	public void onPause() {

		super.onPause();
	}

	public void onStop() {
		super.onStop();
	}

	public void onDestroy() {
		super.onDestroy();
		// TODO : Double check
		// is that thread safe ? as I know , LocationListener runs on different
		// thread ?
		if (this.cityLoc != null)
			this.cityLoc.stopSearch();
	}

}
