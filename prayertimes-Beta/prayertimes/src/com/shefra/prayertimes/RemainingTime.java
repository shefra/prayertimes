package com.shefra.prayertimes;

import java.util.TimerTask;

import android.widget.TextView;

public class RemainingTime extends TimerTask{
	public TextView time;
	public RemainingTime(TextView time){
		this.time = time;
	}
	@Override
	public void run() {
		Integer i = Integer.parseInt(time.getText().toString());
		i = i - 1;	
		this.time.setText(Integer.toString(i));
		
	}
}
