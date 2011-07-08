package com.shefra.prayertimes.manager;

public class City {
	public String cityName,latitude,longitude,timeZone ;
	public int cityNo ;
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
