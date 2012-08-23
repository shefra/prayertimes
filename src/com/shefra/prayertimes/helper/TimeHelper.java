package com.shefra.prayertimes.helper;

public class TimeHelper {
	public static int to24(String time) {
		String[] t = time.split(":");
		String[] AMORPM = t[2].split(" ");
		int houer = Integer.parseInt(t[0]);
		if (AMORPM[1].equals("PM") && houer != 12)
			houer += 12;
		return houer;
	}

	public static int getMinute(String time) {
		String[] t = time.split(":");
		return Integer.parseInt(t[1]);
	}

	// different
	public static int different(int current, int prayer) {
		if (current <= prayer)
			return prayer - current;
		return (prayer + (24 * 3600)) - current;
	}

	// different 2
	public static int different2(int time1, int time2) {
		if (time1 <= time2)
			return time2 - time1;
		return (time2 + (24 * 3600)) - time1;
	}

	// different between two times in 12 hour mode
	// 3:00 - 9:00 pm = 6 hour
	// return seconds
	// public static int different12hour(int time1, int time2) {
	// if(time2>= 12 * 3600)
	// time2 = time2 - 12*3600;
	// if(time1>= 12 * 3600)
	// time1 = time1 - 12*3600;
	//
	// return Math.abs(time2 - time1);
	// }
	public static int getSecond(String time) {
		String[] t = time.split(":");
		String[] s = t[2].split(" ");
		return Integer.parseInt(s[0]);
	}

	public static int getSec(String time) {
		int sec = 0;
		String[] temp = time.split(":");
		sec = Integer.parseInt(temp[0]) * 3600;
		sec += Integer.parseInt(temp[1]) * 60;
		temp = temp[2].split(" ");
		sec += Integer.parseInt(temp[0]);
		return sec;
	}

	public static String getTimeWithoutSeconds(String time){
		String[] t = time.split(":");
		String[] AMORPM = t[2].split(" ");
		String newTime = t[0]+":"+t[1]+" " +AMORPM[1];
		return newTime;
	}
	public static String secondsToTime(double time) {

		int hours = (int) (time / 3600);
		time = time - (hours * 3600);
		int minutes = (int) (time / 60);
		time = time - minutes * 60;
		// int seconds = (int) time;// for visibility matter (Timer issue) TODO
		// is't right ?(MOHAMMED)
		String remTime = "";
		if ((hours % 12) > 9) {
			remTime = (hours % 12) + ":";
		} else {
			remTime = "0" + (hours % 12) + ":";
		}

		if (minutes > 9) {
			remTime += minutes;
		} else {
			remTime += "0" + minutes;
		}

		return remTime;
	}

	public static int getSec(int hh, int mm, int ss) {
		return ((hh * 3600) + (mm * 60) + ss);
	}
}
