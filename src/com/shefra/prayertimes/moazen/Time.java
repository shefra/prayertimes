/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shefra.prayertimes.moazen;

/**
 * 
 * @author Admin
 */
public class Time {
	public Time(double time) {
		this.m_time = convertToTime(time, false);
	}

	public Time(double time, boolean isAM) {
		this.m_time = convertToTime(time, isAM);
	}

	public final String text() {
		return m_time;
	}

	public final int hour() {
		return m_hour;
	}

	public final int minute() {
		return m_minute;
	}

	public final int second() {
		return m_second;
	}

	public final String zone() {
		return m_zone;
	}

	private String convertToTime(double var, boolean isAM) {
		String time = "";
		int ivar = (int) var;
		if (isAM) {
			if ((ivar % 12) < 12 && (ivar % 12) > 0)
				this.m_zone = "AM";
			else
				this.m_zone = "PM";
		} else
			this.m_zone = "PM";

		if (ivar > 12) { // convert hour from 24 to 12
			if (ivar % 12 > 9)
				time += toString(ivar % 12);
			else
				time += "0" + toString(ivar % 12);
			this.m_hour = ivar % 12;
		} else if (ivar == 12) {
			time += toString(ivar);
			this.m_hour = ivar;
		} else {
			if (ivar <= 9)
				time += "0" + toString(ivar);
			else
				time += toString(ivar);
			this.m_hour = ivar;
		}

		time += ":";

		var -= ivar;
		var *= 60;
		ivar = (int) var; // for minutes
		this.m_minute = ivar;
		if (ivar < 10) {
			time += "0" + toString(ivar);
		} else {
			time += toString(ivar);
		}

		time += ":";

		var -= ivar;
		var *= 60;
		ivar = (int) var;// for seconds
		this.m_second = ivar;

		if (ivar < 10) {
			time += "0" + toString(ivar);
		} else {
			time += toString(ivar);
		}
		time += " ";

		time += this.m_zone;
		return time;

	}

	private static String toString(int value) {
		String Stime = Integer.toString(value);
		return Stime;
	}

	private String m_time;

	int m_hour;
	int m_minute;
	int m_second;
	String m_zone;
}
