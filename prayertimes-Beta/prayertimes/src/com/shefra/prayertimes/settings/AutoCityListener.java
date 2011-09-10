//package com.shefra.prayertimes.settings;
//
//import java.util.ArrayList;
//
//import com.shefra.prayertimes.manager.City;
//import com.shefra.prayertimes.manager.Manager;
//import com.shefra.prayertimes.manager.settingAttributes;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.preference.CheckBoxPreference;
//import android.preference.ListPreference;
//import android.preference.Preference;
//import android.preference.PreferenceManager;
//import android.widget.Toast;
//
//public class AutoCityListener implements
//		android.preference.Preference.OnPreferenceChangeListener,LocationListener {
//	private Manager manager;
//	private LocationManager lm;
//	private double longitude;
//	private double latitude ;
//	// private ListPreference cityList;
//	public AutoCityListener(ListPreference cityList, Manager manager,LocationManager lm) {
//		this.manager = manager;
//		this.lm = lm;
//		// this.cityList = cityList;
//	}
//	public void onLocationChanged(Location location) {
//	        longitude = location.getLongitude();
//	        latitude = location.getLatitude();
//	        
//	}
//	public boolean onPreferenceChange(Preference preference, Object newValue) {
//		try{
//		Boolean isChecked = (Boolean) newValue;
//		CheckBoxPreference cp = (CheckBoxPreference) preference;
//		Toast.makeText(manager.getContext(), "E",Toast.LENGTH_LONG);
//		
//		//press OK button
//		if (isChecked.booleanValue()) {
//			ArrayList<City>	cityList = manager.getCityList(1) ;
//			 
//			Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//			if(location != null){
//			longitude  = location.getLongitude();
//			latitude = location.getLatitude();
//			}else{
//			//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
//			}
//			latitude = 24;
//			longitude = 40;
//	        double min=0;
//	        int i =0,pos =0;
//	        for(City city :cityList){
//	                double lat = Double.parseDouble(city.latitude);
//	                double lon = Double.parseDouble(city.longitude);
//	                double pk =  (180/3.14159);
//	                double a1 =  (lat/pk);
//	                double a2 =  (lon/pk);	              
//	                double b1 =  (latitude / pk);
//	                double b2 =  (longitude / pk);
//
//	                
//	                double t1 =  (Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2));
//	                double t2 =  (Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2));
//	                double t3 =  (Math.sin(a1)*Math.sin(b1));
//	                double tt = Math.acos(t1 + t2 + t3);
//	                double dist = (6366000*tt);
//	                if(dist < min || i ==0  )
//	                {  
//	                    min = dist;
//	                    pos = i;
//	                }
//	                i++;
//	          
//	            }
//	        
//	        if(pos < cityList.size() && cityList.get(pos) != null){
//	        	{
//	        		settingAttributes sa = new settingAttributes();
//	    			String cityId = (String) Integer.toString(cityList.get(pos).cityNo);
//	    			sa.city.cityNo = -1;
//	    			if (cityId != null) {
//	    				sa.city.cityNo = Integer.parseInt(cityId);
//	    			}
//	    			if (sa.city.cityNo == -1)
//	    				sa.city.cityNo = 1;
//	    			manager.setSetting(sa);   			
//	        	}
//	        }
//		}
//		}catch(Exception e){
//			e =e;
//		}
//		return true;
//	}
//	public void onProviderDisabled(String provider) {
//		// TODO Auto-generated method stub
//		
//	}
//	public void onProviderEnabled(String provider) {
//		// TODO Auto-generated method stub
//		
//	}
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
