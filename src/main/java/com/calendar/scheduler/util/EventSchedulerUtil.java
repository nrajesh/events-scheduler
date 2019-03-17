package com.calendar.scheduler.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventSchedulerUtil {

	public Date dateFormat(Object inputDate) {
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	    Date outputDate = new Date();
		try {
			outputDate=format.parse((String)inputDate);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return outputDate;
	}
	
	public int intFormat(Object inputInt) {
		int outputInt = 1;
		try {
			outputInt=Integer.valueOf((String)inputInt).intValue();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return outputInt;
	}
}
