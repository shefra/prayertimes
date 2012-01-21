package com.shefra.prayertimes.helper;

public class TimeHelper {
	public static  int to24(String time) {
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

	public static  int diffrent(int current, int prayer) {
		if (current <= prayer)
			return prayer - current;
		return (prayer + (24 * 3600)) - current;
	}

	public static  int getSecond(String time) {
		String[] t = time.split(":");
		String[] s = t[2].split(" ");
		return Integer.parseInt(s[0]);
	}

	public static  int getSec(String time) {
		int sec = 0;
		String[] temp = time.split(":");
		sec = Integer.parseInt(temp[0]) * 3600;
		sec += Integer.parseInt(temp[1]) * 60;
		temp = temp[2].split(" ");
		sec += Integer.parseInt(temp[0]);
		return sec;
	}

	public static String secondsToTime(double time) {

		int hours = (int) (time / 3600);
		time = time - (hours * 3600);
		int minutes = (int) (time / 60);
		time = time - minutes * 60;
		//int seconds = (int) time;// for visibility matter (Timer issue) TODO is't right ?(MOHAMMED)  
		String remTime = hours % 12 + ":" + minutes /* + ":" + seconds */;
		return remTime;
	}

	public static  int getSec(int hh, int mm, int ss) {
		return ((hh * 3600) + (mm * 60) + ss);
	}
}
