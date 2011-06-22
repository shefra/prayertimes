package com.shefra.prayertimes.manager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.shefra.prayertimes.*;
import com.shefra.prayertimes.moazen.PrayerTime;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class Manager extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/com.shefra.prayertimes/databases/";
	private static String DB_NAME = "CountriesDB";
	private Context context;
	private SQLiteDatabase db;

	public Manager(Context applicationContext) {
		super(applicationContext, DB_NAME, null, 1);
		this.context = applicationContext;
	}

	public String getDatabasePath() {
		return DB_PATH;
	}

	public String getDatabaseName() {
		return DB_NAME;
	}

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

	public void createDatabase() throws IOException {
		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}
	}

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

	/* 8064 */
	public azanAttribute getData(int id) {

		db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
		db.setVersion(1);
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);

		azanAttribute aA = new azanAttribute();
		String select = "select cityName,latitude,longitude,timeZone from citiesTable where cityNO ="
				+ id;
		Cursor cur = db.rawQuery(select, null);
		cur.moveToFirst();
		aA.cityName = cur.getString(0);
		aA.latitude = cur.getString(1);
		aA.longitude = cur.getString(2);
		aA.timeZone = cur.getString(3);
		cur.close();
		db.close();
		return aA;

		//return new azanAttribute("test", "1", "1", "1");

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

	public ArrayList<String> getPrayerTimes(String Date) {
		/*
		 * ArrayList<String> prayerList = new ArrayList<String>() ;
		 * settingAttributes sa = this.xmlReader();
		 * 
		 * String [] date = Date.split("/"); PrayerTime prayerTime = new
		 * PrayerTime
		 * (Double.parseDouble(sa.city.longitude),Double.parseDouble(sa
		 * .city.latitude),
		 * Integer.parseInt(sa.city.timeZone),Integer.parseInt(date[0]),
		 * Integer.parseInt(date[1]),Integer.parseInt(date[2]));
		 * prayerTime.setCalender(sa.calender); prayerTime.setMazhab(sa.mazhab);
		 * prayerTime.calculate();
		 * 
		 * prayerList.add(prayerTime.fajrTime().text());
		 * prayerList.add(prayerTime.zuhrTime().text());
		 * prayerList.add(prayerTime.asrTime().text());
		 * prayerList.add(prayerTime.maghribTime().text());
		 * prayerList.add(prayerTime.ishaTime().text()); return prayerList;
		 */
		// TODO: rewrite this method to use SharedPreference
		return null;
	}

	public void setSetting(settingAttributes sa) {
		azanAttribute aA = this.getData(sa.city.cityNo);
		sa.city.latitude = aA.latitude;
		sa.city.longitude = aA.longitude;
		sa.city.timeZone = aA.timeZone;
		// this.xmlWriter(sa);
	}
	/*
	 * public void xmlWriter(settingAttributes sa){ try { DocumentBuilderFactory
	 * documentBuilderFactory = DocumentBuilderFactory.newInstance();
	 * DocumentBuilder documentBuilder; documentBuilder =
	 * documentBuilderFactory.newDocumentBuilder(); Document document =
	 * documentBuilder.newDocument();
	 * 
	 * Element rootElement = document.createElement("prayer");
	 * document.appendChild(rootElement);
	 * 
	 * Element em1 = document.createElement("longitude");
	 * em1.appendChild(document.createTextNode(sa.city.longitude));
	 * rootElement.appendChild(em1);
	 * 
	 * Element em2 = document.createElement("latitude");
	 * em2.appendChild(document.createTextNode(sa.city.latitude));
	 * rootElement.appendChild(em2);
	 * 
	 * Element em3 = document.createElement("zone");
	 * em3.appendChild(document.createTextNode(sa.city.timeZone));
	 * rootElement.appendChild(em3);
	 * 
	 * Element em4 = document.createElement("mazhab");
	 * em4.appendChild(document.createTextNode(sa.mazhab));
	 * rootElement.appendChild(em4);
	 * 
	 * Element em5 = document.createElement("calender");
	 * em5.appendChild(document.createTextNode(sa.calender));
	 * rootElement.appendChild(em5);
	 * 
	 * Element em6 = document.createElement("cityName");
	 * em6.appendChild(document.createTextNode(sa.city.cityName));
	 * rootElement.appendChild(em6);
	 * 
	 * transformerFactory transformerFactory = transformerFactory.newInstance();
	 * Transformer transformer = transformerFactory.newTransformer(); DOMSource
	 * source = new DOMSource(document); OutputStreamWriter out = new
	 * OutputStreamWriter(openFileOutput("test1.xml",0)); // write the contents
	 * on mySettings to the file StreamResult result = new StreamResult(out);
	 * transformer.transform(source, result); // close the file out.close(); }
	 * catch (ParserConfigurationException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } catch (TransformerConfigurationException e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (TransformerException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (Exception e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); }
	 * 
	 * } public settingAttributes xmlReader(){ settingAttributes sa =new
	 * settingAttributes(); DocumentBuilderFactory factory =
	 * DocumentBuilderFactory.newInstance();
	 * 
	 * try { InputStream instream = openFileInput("test.xml"); DocumentBuilder
	 * builder = factory.newDocumentBuilder(); Document dom =
	 * builder.parse(instream); dom.getDocumentElement().normalize(); Element
	 * root = dom.getDocumentElement();
	 * 
	 * NodeList nlList=
	 * root.getElementsByTagName("longitude").item(0).getChildNodes(); Node
	 * nValue = (Node) nlList.item(0); sa.city.longitude=nValue.getNodeValue();
	 * 
	 * nlList= root.getElementsByTagName("latitude").item(0).getChildNodes();
	 * nValue = (Node) nlList.item(0); sa.city.latitude=nValue.getNodeValue();
	 * 
	 * nlList= root.getElementsByTagName("zone").item(0).getChildNodes(); nValue
	 * = (Node) nlList.item(0); sa.city.timeZone=nValue.getNodeValue();
	 * 
	 * nlList= root.getElementsByTagName("mazhab").item(0).getChildNodes();
	 * nValue = (Node) nlList.item(0); sa.mazhab=nValue.getNodeValue();
	 * 
	 * nlList= root.getElementsByTagName("calender").item(0).getChildNodes();
	 * nValue = (Node) nlList.item(0); sa.calender=nValue.getNodeValue();
	 * 
	 * nlList= root.getElementsByTagName("cityName").item(0).getChildNodes();
	 * nValue = (Node) nlList.item(0); sa.city.cityName=nValue.getNodeValue();
	 * 
	 * } catch (Exception e) { throw new RuntimeException(e); } return sa; }
	 */
}
