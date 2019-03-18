package com.calendar.scheduler.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.calendar.scheduler.model.EventObj;
import com.calendar.scheduler.model.ScheduleObj;

public class EventScheduleUtil {

	public Date dateFormat(Object inputDate,String dateType) {
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	    Date outputDate = new Date();
		try {
			if(dateType.equals("end") && "".equals((String)inputDate)) {
				outputDate = format.parse("2099-12-31");
			} else {
				outputDate=format.parse((String)inputDate);
			}
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

	public EventObj createEventObj(Map<String, Object> jpObj) {

		EventObj eventObj = new EventObj(
			eliminateNull((String)jpObj.get("eventName")),
			dateFormat(jpObj.get("startDate"),"start"),
			dateFormat(jpObj.get("endDate"),"end"),
			intFormat(jpObj.get("recurNum")),
			eliminateNull((String)jpObj.get("recurPattern")),
			eliminateNull((String)jpObj.get("weekPattern")),
			eliminateNull((String)jpObj.get("monthPattern")),
			intFormat(jpObj.get("recurFreq"))
		);
		
		return eventObj;
	}

	public String eliminateNull(String inputString) {
		
		return (null==inputString)?"":inputString;
	}

	public ScheduleObj createScheduleObj(Map<String, Object> jpObj) {

		ScheduleObj scheduleObj = new ScheduleObj(
			eliminateNull((String)jpObj.get("id")),
			eliminateNull((String)jpObj.get("eventName")),
			dateFormat(jpObj.get("startDate"),"start")
		);

		return scheduleObj;
	}
}
