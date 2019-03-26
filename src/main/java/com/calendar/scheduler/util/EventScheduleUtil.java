package com.calendar.scheduler.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.calendar.scheduler.SchedulerApplication;
import com.calendar.scheduler.model.EventObj;
import com.calendar.scheduler.model.ScheduleObj;

/**
 * @author nrajesh
 *
 */
public class EventScheduleUtil implements SchedulerConstants {
	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(SchedulerApplication.class);

	private static final Calendar c = Calendar.getInstance();
	
	/**
	 * Method to format input object to a date
	 * @param inputDate
	 * @param dateType In case of exceptions, if this is set to START output date will default to TODAY and setting this to END will default output date to "2099-12-31"
	 * @return @Date value
	 */
	public static LocalDate dateFormat(Object inputDate,String dateType) {
		logger.debug("Input for dateFormat: inputDate = "+inputDate.toString()+" ~ dateType = "+dateType);
	    
		LocalDate outputDate = LocalDate.now();
		
		if(dateType.equals(END) && (EMPTY_STRING.equals(inputDate.toString()) || INVALID_DATE_FORMAT.equals(inputDate.toString()))) {

	    	c.set(2099,11,31);
	    	outputDate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));
		} else if(dateType.equals(START) && EMPTY_STRING.equals(inputDate.toString())){
			outputDate = LocalDate.now();
		} else {
			Date tempDate=new Date();
			try {
				tempDate = new SimpleDateFormat(DATE_FORMAT_SHORT).parse(inputDate.toString());
			} catch (ParseException pe) {
				logger.debug(pe.toString());
			}
			outputDate=tempDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
		
		logger.debug("Result of dateFormat: "+outputDate.toString());
		return outputDate;
	}
	
	/**
	 * Method to format integer 
	 * @param inputInt
	 * @param expnVal which will be defaulted to in case of exceptions
	 * @return Integer value
	 */
	public static int intFormat(Object inputInt, int expnVal) {
		logger.debug("Input for intFormat: inputInt = "+inputInt.toString()+" ~ expnVal = "+expnVal);
		int outputInt = 1;
		try {
			if(null!=inputInt) {
				outputInt=Integer.valueOf((String)inputInt).intValue();
			}
		} catch (NumberFormatException e) {
			outputInt=expnVal;
		}

		logger.debug("Result of intFormat: "+outputInt);
		return outputInt;
	}
	
	/**
	 * Method to format character and defaults to ' ' in case of any exceptions.
	 * @param inputChar
	 * @return Character value
	 */
	public static char charFormat(Object inputChar) {
		logger.debug("Input for charFormat: "+inputChar.toString());
		char outputChar = EMPTY_CHAR;
		try {
			outputChar=eliminateNull((String)inputChar).charAt(0);
		} catch (NumberFormatException e) {
			outputChar = EMPTY_CHAR;
		}

		logger.debug("Result of charFormat: "+outputChar);
		return outputChar;
	}

	/**
	 * Method to create an event object
	 * @param jpObj
	 * @return @EventObj
	 */
	public static EventObj createEventObj(Map<String, Object> jpObj) {
		logger.debug("Input for createEventObj: "+jpObj.toString());
		
		LocalDate startDate = EventScheduleUtil.dateFormat(jpObj.get(START_DATE), START);
		startDate.atStartOfDay(ZoneId.of("UTC"));
		LocalDate endDate = EventScheduleUtil.dateFormat(jpObj.get(END_DATE), END);
		endDate.atStartOfDay(ZoneId.of("UTC"));
		
		EventObj eventObj = new EventObj(
			eliminateNull((String)jpObj.get(EVENT_NAME)),
			startDate,
			endDate,
			intFormat(jpObj.get(RECUR_NUM),1),
			charFormat(jpObj.get(RECUR_PATTERN)),
			eliminateNull((String)jpObj.get(WEEK_PATTERN)),
			intFormat(jpObj.get(START_MONTH),0),
			intFormat(jpObj.get(RECUR_FREQ),1)
		);

		logger.debug("Result of createEventObj: "+eventObj.toString());
		return eventObj;
	}

	/**
	 * Method to eliminate null strings and default them to ""
	 * @param inputString
	 * @return @String
	 */
	public static String eliminateNull(String inputString) {
		logger.debug("Input for eliminateNull: "+inputString);

		String outputString = (null==inputString)?EMPTY_STRING:inputString;
		
		logger.debug("Result of eliminateNull: "+outputString);
		return outputString;
	}

	/**
	 * Method to return a schedule object
	 * @param jpObj
	 * @return @ScheduleObj
	 */
	public static ScheduleObj createScheduleObj(Map<String, Object> jpObj) {
		logger.debug("Input for createScheduleObj: "+jpObj.toString());

//		LocalDate occurrenceDate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
//		occurrenceDate.atStartOfDay(ZoneId.of("UTC"));
		LocalDate occurrenceDate = dateFormat(jpObj.get(START_DATE), START);
		
		ScheduleObj scheduleObj = new ScheduleObj(
			eliminateNull((String)jpObj.get(ID)),
			eliminateNull((String)jpObj.get(EVENT_NAME)),
			occurrenceDate
		);

		logger.debug("Result of createScheduleObj: "+scheduleObj.toString());
		return scheduleObj;
	}

	/**
	 * Method to make an array of selected week days
	 * @param inputDays
	 * @return Integer array of selected week days
	 */
	public static int[] getWeekDays(String inputDays) {
		logger.debug("Input for getWeekDays: "+inputDays);
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

		logger.debug("Result of getWeekDays: "+weekDays);
		return weekDays;
	}
}
