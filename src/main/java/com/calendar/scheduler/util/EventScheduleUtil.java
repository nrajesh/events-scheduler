package com.calendar.scheduler.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import com.calendar.scheduler.model.EventObj;
import com.calendar.scheduler.model.ScheduleObj;

public class EventScheduleUtil {

	public static Date dateFormat(Object inputDate,String dateType) {
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	    format.setTimeZone(TimeZone.getTimeZone("GMT"));
	    Date outputDate = new Date();
		
		try {
			if(dateType.equals("end") && "".equals((String)inputDate)) {
				outputDate = format.parse("2099-12-31");
			} else {
				outputDate=format.parse((String)inputDate);
			}
		} catch (java.text.ParseException e) {
			outputDate = Calendar.getInstance().getTime();
		}
		return outputDate;
	}
	
	public static int intFormat(Object inputInt, int expnVal) {
		int outputInt = 1;
		try {
			outputInt=Integer.valueOf((String)inputInt).intValue();
		} catch (NumberFormatException e) {
			outputInt=expnVal;
		}

		return outputInt;
	}
	
	public static char charFormat(Object inputChar) {
		char outputChar = ' ';
		try {
			outputChar=eliminateNull((String)inputChar).charAt(0);
		} catch (NumberFormatException e) {
			outputChar = ' ';
		}

		return outputChar;
	}

	public static EventObj createEventObj(Map<String, Object> jpObj) {

		EventObj eventObj = new EventObj(
			eliminateNull((String)jpObj.get("eventName")),
			dateFormat(jpObj.get("startDate"),"start"),
			dateFormat(jpObj.get("endDate"),"end"),
			intFormat(jpObj.get("recurNum"),1),
			charFormat(jpObj.get("recurPattern")),
			eliminateNull((String)jpObj.get("weekPattern")),
			eliminateNull((String)jpObj.get("monthPattern")),
			intFormat(jpObj.get("recurFreq"),1)
		);
		
		return eventObj;
	}

	public static String eliminateNull(String inputString) {
		
		return (null==inputString)?"":inputString;
	}

	public static ScheduleObj createScheduleObj(Map<String, Object> jpObj) {

		ScheduleObj scheduleObj = new ScheduleObj(
			eliminateNull((String)jpObj.get("id")),
			eliminateNull((String)jpObj.get("eventName")),
			(Date)jpObj.get("startDate")
		);

		return scheduleObj;
	}

	public static int[] getWeekDays(String inputDays) {
		int[] weekDays=null;
		int itr = 0, weekDay=0;
		StringTokenizer strTkn = new StringTokenizer(inputDays,",");
		
		try {
			weekDays = new int[strTkn.countTokens()];
			while (strTkn.hasMoreElements()) {
				weekDay = intFormat((String)strTkn.nextElement(),1);
				weekDays[itr] = weekDay;
				itr++;
			}
		} catch (NumberFormatException e) {
			weekDays[itr] = 1;
		}
		
		return weekDays;
	}
}
