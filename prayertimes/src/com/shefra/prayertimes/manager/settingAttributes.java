package com.shefra.prayertimes.manager;

public class settingAttributes {
	public String mazhab,calender;
	public City city ;
	Country country ;
	public settingAttributes() {
		city = new City();
		country = new Country();
	}
	public settingAttributes(String cityNameC ,
	String mazhabC,String calenderC , int cityNoC ,String countryNameC , int countryNoC){
		city = new City();
		city.cityName = cityNameC;
		city.cityNo = cityNoC;
		country = new Country(countryNameC,countryNoC);
		mazhab = mazhabC;
		calender = calenderC;
	} 
	
	public settingAttributes(String cityNameC , String latitudeC,String longitudeC,String timeZoneC,
			String mazhabC,String calenderC,int cityNoC ,String countryNameC, int countryNoC){
		city = new City(cityNameC,latitudeC,longitudeC,timeZoneC,cityNoC);
		country = new Country(countryNameC,countryNoC);
		mazhab = mazhabC;
		calender = calenderC;
		
	}
}
