package com.shefra.prayertimes.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import com.shefra.prayertimes.manager.City;
import com.shefra.prayertimes.manager.Country;
import com.shefra.prayertimes.manager.azanAttribute;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

public class DatabaseHelper  extends SQLiteOpenHelper {
	private static String DB_PATH = "/data/data/com.shefra.prayertimes/databases/";
	private static String DB_NAME = "CountriesDB";
	private Context context;
	private SQLiteDatabase db;
	
	public DatabaseHelper(Context applicationContext) {
		super(applicationContext, DB_NAME, null, 1);
		this.context = applicationContext;
	}
	
	// -----------DataBase methods-----------//
	public String getDatabasePath() {
		return DB_PATH;
	}

	public String getDatabaseName() {
		return DB_NAME;
	}

	// used to know if the database is installed or not
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	// copy the database from assets folder to data folder (system folder)
	// Android system does not allow to access the database from assets folder

	// BE CAREFUL : the file must not be bigger then 1 Mega byte :(
	public void createDatabase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
		} else {
			// By calling this method an empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our new database.
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	// read createDatabase method for more comments
	public void copyDataBase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = this.context.getAssets().open(DB_NAME);
		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	@Override
	public synchronized void close() {
		if (db != null)
			db.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	// get country details based on country id 
	// useful when read country id frrom xml preference
	
	public Country getCountry(int countrId) {
		SQLiteDatabase db;
		Country country = new Country();

		db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
		db.setVersion(1);
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);

		Cursor cur = db.query("country", null, "country_id=" + countrId, null,
				null, null, null);
		cur.moveToFirst();
		while (cur.isAfterLast() == false) {

			country.countryNo = cur.getInt(0);
			// not all the country has Arabic name
			// so use English name instead
			// prevents NULL error that happens when put NULL
			// into a view object ( e.g. ListView )
			if (cur.getString(2) != null)
				country.countryName = cur.getString(2);
			else
				country.countryName = cur.getString(1);
			cur.moveToNext();
		}
		cur.close();
		db.close();
		return country;
	}

	/// get city details based on city id
	// useful when read city id from preference file ( xml file/ setting file)
	public City getCity(int cityId) {
		City city = new City();

		db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
		db.setVersion(1);
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);

		Cursor cur = db.query("citiesTable", new String[] { "cityNO",
				"cityName", "cityNameAr" }, "cityNO=" + cityId, null, null,
				null, null);
		cur.moveToFirst();
		while (cur.isAfterLast() == false) {
			city.cityNo = cur.getInt(0);
			// not all the city has Arabic name
			// so use English name instead
			// prevents NULL error that happens when put NULL
			// into a view object ( e.g. ListView )
			if (cur.getString(2) != null)
				city.cityName = cur.getString(2);
			else
				city.cityName = cur.getString(1);
			cur.moveToNext();
		}
		cur.close();
		db.close();
		return city;
	}
	
	// read the preference file( xml file)
	// to get the selected city id 
	// then get city detailed based on that id
	public City getCurrentCity() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String cityId = pref.getString("city", "1");
		City city = this.getCity(Integer.parseInt(cityId));
		return city;

	}

	public azanAttribute getData(int id) {
		db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
		db.setVersion(1);
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		
		azanAttribute aA = new azanAttribute();
		String select = "select cityName,latitude,longitude,timeZone,country_id from citiesTable where cityNO ="
				+ id;
		Cursor cur = db.rawQuery(select, null);
		cur.moveToFirst();
		
		aA.cityName = cur.getString(0);
		aA.latitude = cur.getString(1);
		aA.longitude = cur.getString(2);
		aA.timeZone = cur.getString(3);
		aA.countryNo = cur.getString(4);
		
		cur.close();
		db.close();
		return aA;
	}
	
	// -----------get methods-----------//
	// git city list based on country id
	// if id = -1 => means return all cities in the database
	public ArrayList<City> getCityList(int id) {
		ArrayList<City> city = new ArrayList<City>();

		db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
		db.setVersion(1);
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);

		String whereClause = null;
		if (id != -1) {
			whereClause = "country_id=" + id;
		}
		Cursor cur = db.query("citiesTable", new String[] { "cityNO",
				"cityName", "cityNameAr", "latitude", "longitude" },
				whereClause, null, null, null, null);
		cur.moveToFirst();
		while (cur.isAfterLast() == false) {
			City c = new City();
			c.cityNo = cur.getInt(0);

			// not all the city has Arabic name
			// so use English name instead
			// prevents NULL error that happens when put NULL
			// into a view object ( e.g. ListView )
			if (cur.getString(2) != null)
				c.cityName = cur.getString(2);
			else
				c.cityName = cur.getString(1);

			c.latitude = cur.getString(3);
			c.longitude = cur.getString(4);
			city.add(c);
			cur.moveToNext();
		}
		cur.close();
		db.close();
		return city;
	}

	// get all country from the database
	public ArrayList<Country> getCountryList() {
		SQLiteDatabase db;
		ArrayList<Country> country = new ArrayList<Country>();

		db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
		db.setVersion(1);
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);

		Cursor cur = db.query("country", null, null, null, null, null, null);
		cur.moveToFirst();

		while (cur.isAfterLast() == false) {
			Country c = new Country();
			c.countryNo = cur.getInt(0);
			// not all the country has Arabic name
			// so use English name instead
			// prevents NULL error that happens when put NULL
			// into a view object ( e.g. ListView )
			if (cur.getString(2) != null)
				c.countryName = cur.getString(2);
			else
				c.countryName = cur.getString(1);

			country.add(c);
			cur.moveToNext();
		}
		cur.close();
		db.close();
		return country;
	}
	
}
