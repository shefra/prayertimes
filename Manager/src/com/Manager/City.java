package com.Manager;

public class City {
	String cityName,latitude,longitude,timeZone ;
	int cityNo ;
	public City(){
		
	}
	public City(String cityNameC ,String latitudeC,String longitudeC,
			String timeZoneC, int cityNoC){
		cityName = cityNameC;
		cityNo = cityNoC ;
		latitude = latitudeC;
		longitude = longitudeC;
		timeZone = timeZoneC ;
	}

}
