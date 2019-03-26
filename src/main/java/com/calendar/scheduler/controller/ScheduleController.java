package com.calendar.scheduler.controller;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.calendar.scheduler.SchedulerApplication;
import com.calendar.scheduler.dto.IScheduleMongoDB;
import com.calendar.scheduler.model.ScheduleObj;
import com.calendar.scheduler.util.EventScheduleUtil;
import com.calendar.scheduler.util.SchedulerConstants;

@Controller
public class ScheduleController implements SchedulerConstants {
	/**
	 * @author nrajesh
	 *
	 */
	public class HomeController {

		@RequestMapping(value = "/")
		public String index() {
			return INDEX_HTML;
		}
	}

	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(SchedulerApplication.class);
	private DateFormat format = new SimpleDateFormat(DATE_FORMAT_SHORT);
	Calendar startCalendar = new GregorianCalendar();
	Calendar endCalendar = new GregorianCalendar();
	
	/**
     * Autowired annotation to resolve and inject IScheduleMongoDB interface
	 */
	@Autowired
	private IScheduleMongoDB schedulerMongo;

	/**
	 * Method to fetch all schedules that gets invoked on receiving the /fetchAllSchedules message
	 * @return List of @ScheduleObj
	 */
	@RequestMapping(value = "/fetchAllSchedules", produces = "application/json; charset=UTF-8")
	@ResponseBody
	private List<ScheduleObj> fetchAllSchedules() {
		logger.debug("Executing fetchAllSchedules");
		
		return schedulerMongo.findAll();
	}
	
	/**
	 * Method to fetch schedules for a particular event that gets invoked on receiving the /fetchSchedules message
	 * @param Single @EventObj
	 * @return List of @ScheduleObj is conveyed to the topic URL used as @SendTo destination for the reply
	 */
	@MessageMapping(value="/fetchSchedules")
	@ResponseBody
	@SendTo(value="/topic/fetchSchedules")
	public List<ScheduleObj> fetchSchedules(@RequestBody String searchObj) {
		logger.debug("Input for fetchSchedules: "+searchObj);
		JSONParser jp = new JSONParser(searchObj.toString());
		
		Map<String, Object> jpObj;

		List<ScheduleObj> rsltLst=new ArrayList<ScheduleObj>();
		int cntr=0,cntOccurances=0;
		
		try {
			jpObj = jp.object();

			// Obtain event object information and store in local variables
			cntOccurances = EventScheduleUtil.intFormat(jpObj.get(NUM_SCHEDULES),1);
			LocalDate startDate = EventScheduleUtil.dateFormat(jpObj.get(SEARCH_START_DATE),START);
			String eventName = EventScheduleUtil.eliminateNull((String)jpObj.get(SEARCH_EVENT_NAME));
	
			for(ScheduleObj schObj : fetchAllSchedules()) {
				if(!EMPTY_STRING.equals(eventName)
						&& (schObj.getEventName().equals(eventName))) {
	
					// Perform fetch of schedules for a specific event up to number of occurrences or from start date specified
					if((!schObj.getOccurrenceDate().isBefore(startDate)
							&& cntOccurances==0) 
							|| (cntr < cntOccurances && 
									!schObj.getOccurrenceDate().isBefore(startDate))) {
						rsltLst.add(schObj);
	//					}
				} else if(EMPTY_STRING.equals(eventName)) {
					// Perform fetch of all schedules up to number of occurrences or from start date specified
					if(cntr < cntOccurances || !schObj.getOccurrenceDate().isBefore(startDate)) {
						rsltLst.add(schObj);
					}
				}
				cntr++;
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug("Result of fetchSchedules: "+rsltLst.toString());
		return rsltLst;
	}
	
	/**
	 * Method to fetch the count of schedules for an event that gets invoked on receiving the /fetchScheduleCount message
	 * @param searchEvtName specific event name
	 * @return count of schedules for an event is conveyed to the topic URL used as @SendTo destination for the reply
	 */
	@MessageMapping(value="/fetchScheduleCount")
	@ResponseBody
	@SendTo(value="/topic/fetchScheduleCount")
	public long fetchScheduleCount(@RequestBody String searchEvtName) {
		logger.debug("Input for fetchScheduleCount: "+searchEvtName);
		JSONParser jp = new JSONParser(searchEvtName.toString());

		Map<String, Object> jpObj;
		long result=0;
		List<ScheduleObj> lstObj;
		
		try {
			jpObj = jp.object();
			
			lstObj = fetchAllSchedules();
			
			for(ScheduleObj schObj : lstObj) {
				
				// Identify specific event name within list of all schedules
				if(schObj.getEventName().contains((String)jpObj.get(EVENT_NAME))) {
					result++;
				}
			}
		} catch (ParseException pe) {
			logger.debug(pe.toString());
		}

		logger.debug("Result of fetchScheduleCount: "+result);
		return result;
	}

	/**
	 * Method to insert a schedule  that gets invoked on receiving the /insertSchedule message 
	 * @param Single @EventObj
	 * @return ScheduleObj populated with id
	 */
	@MessageMapping(value = "/insertSchedule")
	@ResponseBody
	public ScheduleObj insertSchedule(@RequestBody String selectedData) {
		logger.debug("Input for insertSchedule: "+selectedData);
		JSONParser jp = new JSONParser(selectedData.toString());
		format.setTimeZone(TimeZone.getTimeZone(DEFAULT_TZ));
	    
		ScheduleObj scheduleObj = new ScheduleObj();
		try {
			Map<String, Object> jpObj = jp.object();
			int recurNum = EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get(RECUR_NUM)),0);
			int recurFreq = EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get(RECUR_FREQ)),1);
			LocalDate startDate = EventScheduleUtil.dateFormat(jpObj.get(START_DATE), START);
			LocalDate endDate = EventScheduleUtil.dateFormat(jpObj.get(END_DATE), END);
			char recurPattern = EventScheduleUtil.charFormat(jpObj.get(RECUR_PATTERN));
			LocalDate currDate = startDate;
			
			switch (recurPattern) {
			
				case 'd':
					long dateDiff = 0;
					if(recurNum==0) {
						dateDiff = ChronoUnit.DAYS.between(startDate,endDate);
					} else {
						endDate = startDate.plusDays(recurNum*recurFreq);
					}
					
					logger.debug("AND THE DIFFERENCE IS: "+dateDiff);
					while(!currDate.isAfter(endDate)) {
						jpObj.put(START_DATE, currDate);
						scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);

						scheduleObj = schedulerMongo.save(scheduleObj);
						currDate = currDate.plusDays(recurFreq);
					}
					break;
			}

		} catch (NullPointerException npe) {
			logger.debug(npe.toString());
		} catch (ParseException pe) {
			logger.debug(pe.toString());
		}
		logger.debug("Result of insertSchedule: "+scheduleObj.toString());
		return scheduleObj;
	}

	/**
	 * Method to purge all schedules from DB
	 * @return Returns a string to confirm schedules are purged
	 */
	@RequestMapping(value = "/purgeAllSchedules", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String purgeAllSchedules() {
		logger.debug("Entered purgeAllSchedules");

		schedulerMongo.deleteAll();
		
		logger.debug("Exited purgeAllSchedules");
		return "Purged";
	}
}
