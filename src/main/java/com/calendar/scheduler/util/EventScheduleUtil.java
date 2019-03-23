package com.calendar.scheduler.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import com.calendar.scheduler.model.EventObj;
import com.calendar.scheduler.model.ScheduleObj;

public class EventScheduleUtil implements SchedulerConstants {

	public static Date dateFormat(Object inputDate,String dateType) {
	    DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
	    format.setTimeZone(TimeZone.getTimeZone(DEFAULT_TZ));
	    Date outputDate = new Date();
		
		try {
			if(dateType.equals(END) && EMPTY_STRING.equals(inputDate.toString())) {
				outputDate = format.parse(END_DATE_LONG);
			} else if(dateType.equals(START) && EMPTY_STRING.equals(inputDate.toString())){
				outputDate = Calendar.getInstance().getTime();
			} else {
				outputDate=format.parse(inputDate.toString()+" CET");
			}
		} catch (java.text.ParseException e) {
			if(null!=inputDate.toString() && inputDate.toString().length()>20) {
				try {
					outputDate = new SimpleDateFormat(DATE_FORMAT_FULL, Locale.ENGLISH).parse(inputDate.toString());
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					outputDate = Calendar.getInstance().getTime();
				}
			} else {
				outputDate = Calendar.getInstance().getTime();
			}
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
		char outputChar = EMPTY_CHAR;
		try {
			outputChar=eliminateNull((String)inputChar).charAt(0);
		} catch (NumberFormatException e) {
			outputChar = EMPTY_CHAR;
		}

		return outputChar;
	}

	public static EventObj createEventObj(Map<String, Object> jpObj) {

		EventObj eventObj = new EventObj(
			eliminateNull((String)jpObj.get(EVENT_NAME)),
			dateFormat(jpObj.get(START_DATE),START),
			dateFormat(jpObj.get(END_DATE),END),
			intFormat(jpObj.get(RECUR_NUM),1),
			charFormat(jpObj.get(RECUR_PATTERN)),
			eliminateNull((String)jpObj.get(WEEK_PATTERN)),
			intFormat(jpObj.get(START_MONTH),0),
			intFormat(jpObj.get(RECUR_FREQ),1)
		);
		
		return eventObj;
	}

	public static String eliminateNull(String inputString) {
		
		return (null==inputString)?EMPTY_STRING:inputString;
	}

	public static ScheduleObj createScheduleObj(Map<String, Object> jpObj) {

		ScheduleObj scheduleObj = new ScheduleObj(
			eliminateNull((String)jpObj.get(ID)),
			eliminateNull((String)jpObj.get(EVENT_NAME)),
			(Date)jpObj.get(START_DATE)
		);

		return scheduleObj;
	}

	public static int[] getWeekDays(String inputDays) {
		int[] weekDays=null;
		int itr = 0, weekDay=0;
		StringTokenizer strTkn = new StringTokenizer(inputDays,COMMA);
		
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
