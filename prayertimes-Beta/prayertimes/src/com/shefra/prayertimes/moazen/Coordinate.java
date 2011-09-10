/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shefra.prayertimes.moazen;
/**
 *
 * @author Admin
 */ 
public class Coordinate {
	public double longitude;
	public double latitude;
	public int zone;
        public Coordinate(){}
	public Coordinate(double longitude,double latitude,int zone){
	this.longitude = longitude;
	this.latitude = latitude;
	this.zone = zone;
	}
}
 