package com.shefra.prayertimes.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.CityLocationListener;
import com.shefra.prayertimes.manager.Manager;
import com.shefra.prayertimes.manager.Preference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class CityFinder extends Activity {

	private Button findCityButton;
	ProgressBar progressDialog;
	// private CityFinderTask cityFinderTask;
	private TextView textViewResult;
	private CityLocationListener cityLoc;
	private CityFinderTask cityFinderTask;

	int TIME_ZONE = 0, CITY_DATA = 1;
	String[] url = new String[2];
	String[] data = new String[2];
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cityfinder);
		progressDialog = (ProgressBar) findViewById(R.id.progressBar1);
		progressDialog.setVisibility(View.GONE);
		findCityButton = (Button) findViewById(R.id.findCity);
		textViewResult = (TextView) findViewById(R.id.textViewResult);

		findCityButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					cityLoc = new CityLocationListener(CityFinder.this);
					cityLoc.startSearch();
					progressDialog.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e = e;
				}
			}
		});

	}

	public void stopSearch(Location location) {
		// if (cityFinderTask != null) {
		// is it safe to change the done var from UI thread?
		// in another way: is it necessary to do that manually ?
		// cityFinderTask.setDone(true);

		// cityFinderTask.cancel(true);

		// }
		cityFinderTask = new CityFinderTask();
		CityFinder.this.cityFinderTask.execute(new Location[] { location });

	}

	private class CityFinderTask extends AsyncTask<Location, String, String> {
		private boolean done;
		private Location loc;
		@Override
		protected void onPreExecute() {
			try {
				// done = false;
				super.onPreExecute();
				// cityLoc = new CityLocationListener(CityFinder.this);
				// cityLoc.startSearch();
				// progressDialog.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Log.e("", e.getMessage(), e.getCause());
			}
		}

		@Override
		protected String doInBackground(Location... params) {
			try {
				// TODO: wait 10 seconds then stop the process and invoke
				// LocationListener stopper.
				// cityLoc.stopSearch();
				Location location = params[0];
				// Manager manager = new Manager(CityFinder.this);
				this.loc = location;
				// manager.findCurrentCity(location.getLatitude(),
				// location.getLongitude());
				//Log.i("H2: ", pos.City);
			} catch (Exception e) {
				Log.e("", e.getMessage(), e.getCause());
			}
			return null;
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
	

			return city;
		}

		public int getTimeZone(String TimeZoneContent)
				throws ParserConfigurationException, ClientProtocolException,
				Exception, SAXException {
			
			CharacterData cd;
			try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(TimeZoneContent));
			Document doc = db.parse(is);

			NodeList nodeList = doc.getElementsByTagName("offset");
			 Element n = (Element) nodeList.item(0);
			String data = getCharacterDataFromElement(n);
			return Integer.valueOf(data);
			}
	        catch(Exception e){
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
						temp.country.name = temp.country.longName ;
						temp.country.shortName = AddCombList.get(i).getString(
								"short_name");
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
					Log.e("tomaanina","Time Out");
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

		protected void onPostExecute(String result) {
			progressDialog.setVisibility(View.GONE);
			City city = null;
			try {
				city = this.getPosition(
						loc.getLongitude(), loc.getLatitude());
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			textViewResult.setText("Done:" + city.name + " , "
					+ loc.getLongitude() + " , " + loc.getLatitude());
			Manager manager = new Manager(CityFinder.this);
			manager.updateCity(city,CityFinder.this);
		}


	}
	public static String getCharacterDataFromElement(Element e)
	{
	    Node child = e.getFirstChild();
	    if (child instanceof CharacterData)
	    {
	        CharacterData cd = (CharacterData) child;
	        return cd.getData();
	    }
	    return "";
	}


}
