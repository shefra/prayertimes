/*
 * settingAttributes Class,it used to store the setting attributes
 * Constructors:
 * 	1- Default.
 * 	2- TODO (KHALID).
 * 	3- TODO (KHALID).
 */ 
package com.shefra.prayertimes.manager;

public class SettingAttributes {
	public String mazhab,calender , season;
	public City city ;
	public Country country ;
	public SettingAttributes() {
		city = new City();
		country = new Country();
	}
	public SettingAttributes(String cityNameC ,
	String mazhabC,String calenderC , String seasonC, int cityNoC ,String countryNameC , int countryNoC){
		city = new City();
		city.cityName = cityNameC;
		city.cityNo = cityNoC;
		country = new Country(countryNameC,countryNoC);
		mazhab = mazhabC;
		calender = calenderC;
		season = seasonC ;
	} 
	
	public SettingAttributes(String cityNameC , String latitudeC,String longitudeC,String timeZoneC,
			String mazhabC,String calenderC, String seasonC , int cityNoC ,String countryNameC, int countryNoC){
		city = new City(cityNameC,latitudeC,longitudeC,timeZoneC,cityNoC);
		country = new Country(countryNameC,countryNoC);
		mazhab = mazhabC;
		calender = calenderC;
		season = seasonC ;
		
	}
}
 