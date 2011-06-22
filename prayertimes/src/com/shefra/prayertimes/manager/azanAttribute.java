package com.shefra.prayertimes.manager;

public class azanAttribute {
	public String cityName , latitude,longitude,timeZone;
	public azanAttribute() {
	}
	public azanAttribute(String cityNameC , String latitudeC,String longitudeC,String timeZoneC){
		cityName = cityNameC;
		latitude = latitudeC;
		longitude = longitudeC;
		timeZone = timeZoneC;
	}
}
