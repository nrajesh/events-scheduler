package com.events.scheduler.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.events.scheduler.SchedulerApplication;
import com.events.scheduler.model.EventObj;
import com.events.scheduler.model.ScheduleObj;

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
	    
		LocalDate outputDate = LocalDate.now();
		if(null!=inputDate) {
			if(dateType.equals(END) && (EMPTY_STRING.equals(inputDate.toString()) || UNDEFINED.equals(inputDate.toString()))) {
	
		    	c.set(2099,11,31);
		    	outputDate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));
			} else if(dateType.equals(START) && (EMPTY_STRING.equals(inputDate.toString()) || UNDEFINED.equals(inputDate.toString()))){
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
		} else {
			if(dateType.equals(END)) {
				c.set(2099,11,31);
		    	outputDate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));
			} else if(dateType.equals(START)) {
				outputDate = LocalDate.now();
			}
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
		int outputInt = expnVal;
		try {
			if(null!=inputInt) {
				logger.debug("Input for intFormat: inputInt = "+inputInt.toString()+" ~ expnVal = "+expnVal);
				outputInt=Integer.valueOf((String)inputInt).intValue();
			}
		} catch (NumberFormatException nfe) {
			outputInt=expnVal;
			logger.debug(nfe.toString());
		} catch (ClassCastException ce) {
			outputInt=expnVal;
			logger.debug(ce.toString());
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
		} catch (NumberFormatException nfe) {
			outputChar = EMPTY_CHAR;
			logger.debug(nfe.toString());
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
			intFormat(jpObj.get(RECUR_NUM),0),
			charFormat(jpObj.get(RECUR_PATTERN)),
			eliminateNull((String)jpObj.get(WEEK_PATTERN)),
			getMonth((String)jpObj.get(START_MONTH)),
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
	 * Method to make Integer array of selected week days
	 * @param inputWeekDays
	 * @return Integer array of selected week days
	 */
	public static int[] getWeekDays(String inputWeekDays) {
		logger.debug("Input for getWeekDays: "+inputWeekDays);
		int[] weekDays=null;
		int itr = 0;
		StringTokenizer strTkn = null;
		String tknVal = EMPTY_STRING;
		
		try {
			if(null!=inputWeekDays) {
				strTkn = new StringTokenizer(inputWeekDays,COMMA);
				weekDays = new int[strTkn.countTokens()];
				while (strTkn.hasMoreElements()) {
					tknVal = strTkn.nextToken();
					final Map<Integer, String> weekDayMap = new HashMap<Integer, String>();
					weekDayMap.put(1,"mon");
					weekDayMap.put(2,"tue");
					weekDayMap.put(3,"wed");
					weekDayMap.put(4,"thu");
					weekDayMap.put(5,"fri");
					weekDayMap.put(6,"sat");
					weekDayMap.put(7,"sun");
					
					for(int tempKey=1;tempKey<=weekDayMap.size();) {
						String tempVal=weekDayMap.get(tempKey);
						if(tknVal.toLowerCase().startsWith(tempVal)) {
							weekDays[itr] = tempKey;
							itr++;
						}
						tempKey++;
					}
				}
			} else {
				weekDays = new int[1];
				weekDays[0] = 1;
			}
		} catch (NumberFormatException nfe) {
			logger.debug(nfe.toString());
		}

		logger.debug("Result of getWeekDays: "+weekDays);
		return weekDays;
	}

	/**
	 * Method to extract Integer value of selected month
	 * @param inputMonth
	 * @return Integer value of selected month
	 */
	public static int getMonth(String inputMonth) {
		logger.debug("Input for getMonths: "+inputMonth);

		int monthVal = 0;
		
		try {
			if(null!=inputMonth) {
				monthVal = Integer.valueOf((String)inputMonth).intValue();
			} else {
				monthVal = 1;
			}
		} catch (NumberFormatException e) {
			final Map<Integer, String> monthMap = new HashMap<Integer, String>();
			monthMap.put(1,"jan");
			monthMap.put(2,"feb");
			monthMap.put(3,"mar");
			monthMap.put(4,"apr");
			monthMap.put(5,"may");
			monthMap.put(6,"jun");
			monthMap.put(7,"jul");
			monthMap.put(8,"aug");
			monthMap.put(9,"sep");
			monthMap.put(10,"oct");
			monthMap.put(11,"nov");
			monthMap.put(12,"dec");
			
			for(int tempKey=1;tempKey<=monthMap.size();) {
				String tempVal=monthMap.get(tempKey);
				if(inputMonth.toLowerCase().startsWith(tempVal)) {
					monthVal = tempKey;
				}
				tempKey++;
			}
		}
		logger.debug("Result of getMonths: "+monthVal);
		return monthVal;
	}
}
