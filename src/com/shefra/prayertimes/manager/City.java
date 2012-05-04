package com.shefra.prayertimes.manager;

public class City {
	public String name = null;
	public String id   = null;
	public int timeZone = -999;
	public String longitude;
	public String latitude ;
	public Country country;
	public City(){
		this.country = new Country();
	}
}
