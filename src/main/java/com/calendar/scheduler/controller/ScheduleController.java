package com.calendar.scheduler.controller;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
	private DateFormat format = new SimpleDateFormat(DATE_FORMAT_SHORT);
	Calendar startCalendar = new GregorianCalendar();
	Calendar endCalendar = new GregorianCalendar();
	
	private Date start, end, currOccurance,nextOccurance;
	private int  increment = 0;
	private int recurNum = 0;
	
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
		JSONParser jp = new JSONParser(searchObj.toString());
		
		Map<String, Object> jpObj;

		List<ScheduleObj> rsltLst=new ArrayList<ScheduleObj>();
		int cntr=0,cntOccurances=0;
		
		try {
			jpObj = jp.object();

			// Obtain event object information and store in local variables
			cntOccurances = EventScheduleUtil.intFormat(jpObj.get(NUM_SCHEDULES),1);
			Date startDate = EventScheduleUtil.dateFormat(jpObj.get(SEARCH_START_DATE),START);
			String eventName = EventScheduleUtil.eliminateNull((String)jpObj.get(SEARCH_EVENT_NAME));
			
			for(ScheduleObj schObj : fetchAllSchedules()) {
				if(!EMPTY_STRING.equals(eventName)
						&& (schObj.getEventName().equals(eventName))) {

					// Perform fetch of schedules for a specific event up to number of occurrences or from start date specified
					if((!schObj.getOccuranceDate().before(startDate)
							&& cntOccurances==0) 
							|| (cntr < cntOccurances && 
									!schObj.getOccuranceDate().before(startDate))) {
						rsltLst.add(schObj);
					}
				} else if(EMPTY_STRING.equals(eventName)) {
					// Perform fetch of all schedules up to number of occurrences or from start date specified
					if(cntr < cntOccurances || !schObj.getOccuranceDate().before(startDate)) {
						rsltLst.add(schObj);
					}
				}
				cntr++;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		JSONParser jp = new JSONParser(selectedData.toString());
		format.setTimeZone(TimeZone.getTimeZone(DEFAULT_TZ));
	    
		ScheduleObj scheduleObj = new ScheduleObj();
		try {
			Map<String, Object> jpObj = jp.object();
			recurNum = EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get(RECUR_NUM)),0);
			increment = EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get(RECUR_FREQ)),1);

			start = EventScheduleUtil.dateFormat(jpObj.get(START_DATE), START);
			end = EventScheduleUtil.dateFormat(jpObj.get(END_DATE), END);

			if(recurNum == 0 && !format.parse(END_DATE_LONG).before(end)) {
				recurNum++;
			}
			
			// Handle a single schedule insert
			if (recurNum == 1) {
				while (end.after(start)) {
					if(!format.parse(END_DATE_LONG).before(end) || end.after(start)) {
						 scheduleObj = insertScheduleRecords(scheduleObj, jpObj, recurNum, increment);
							increment++;
					} else {
						break;
					}
				}
			} else {
				// Handle multiple schedule inserts based on frequency and insert count specified in input request
				while (increment != recurNum) {
					if(!EventScheduleUtil.dateFormat(END_DATE_LONG,END).before(end)) {
						scheduleObj = insertScheduleRecords(scheduleObj, jpObj, recurNum, increment);
						increment++;
					} else {
						break;
					}
				}
			}
			
			// Reset important member variables to handle next incoming request
			currOccurance = nextOccurance = null;
			end = format.parse(END_DATE_LONG);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scheduleObj;
	}

	/**
	 * Method logic to insert a schedule based on event type
	 * @param A single @ScheduleObj without id
	 * @param Map of key value pairs received from in-bound request
	 * @param Number of occurrences of event
	 * @param Frequency of event
	 * @return A single @ScheduleObj populated with id after save operation
	 */
	private ScheduleObj insertScheduleRecords(ScheduleObj scheduleObj, Map<String, Object> jpObj, int recurNum, int increment) {

		int recurFreq = EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get(RECUR_FREQ)),1);
		
		Calendar c = Calendar.getInstance();
		
		char recurPattern = EventScheduleUtil.charFormat(jpObj.get(RECUR_PATTERN));
		
		int[] weekDays = EventScheduleUtil.getWeekDays((String)jpObj.get(WEEK_PATTERN));
		int month = EventScheduleUtil.intFormat(String.valueOf((BigInteger)jpObj.get(START_MONTH)),0);

		int temp=0,diffYear=0;
		// Initialise calendar object to handle each incoming request
		if(null==nextOccurance) {
			c.setTime(start);
		} else {
			c.setTime(nextOccurance);
		}
		
		switch (recurPattern) {
		
			case 'd':
				// Logic to handle event type days
					
				currOccurance = c.getTime();
				jpObj.put(START_DATE, currOccurance);
				scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);

				scheduleObj = schedulerMongo.save(scheduleObj);
				//end = (Date)jpObj.get(START_DATE);
				c.add(Calendar.DATE, recurFreq);
				nextOccurance = c.getTime();
				start = nextOccurance;
				
				break;
				
			case 'w':
				// Logic to handle event type weeks 
				
				for(int weekDay: weekDays) {
					// If number of occurrences is 0 i.e. end date is 2099-12-31, get number of occurrences between start and end
					if(EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get(RECUR_NUM)),0)==0) {
						startCalendar.setTime(start);
						endCalendar.setTime(end);
						
						diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
						
						recurNum = ((diffYear * 52) + endCalendar.get(Calendar.WEEK_OF_MONTH) - startCalendar.get(Calendar.WEEK_OF_MONTH))/recurFreq;
					}

					c.setTime(start);
					while(c.get(Calendar.WEEK_OF_MONTH)!=weekDay) {
						c.add(Calendar.DATE,1);
					}

					for(int cntr=0;cntr<=recurNum;cntr++) {
						currOccurance = c.getTime();
						jpObj.put(START_DATE, currOccurance);

						// Push week to next occurrence based on specified event frequency
						if(currOccurance.before(end) && weekDay==c.get(Calendar.DAY_OF_WEEK)) {
							scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);
		
							scheduleObj = schedulerMongo.save(scheduleObj);
							c.add(Calendar.WEEK_OF_MONTH, recurFreq);
							temp+=1;
						} else {

							c.add(Calendar.WEEK_OF_MONTH, 1);
						}
						nextOccurance = c.getTime();
						start = nextOccurance;
						if(temp==recurNum) {
							endCalendar.add(Calendar.WEEK_OF_MONTH,1);
							end=endCalendar.getTime();
							recurNum = increment;
							break;
						}

						// Push week to next occurrence based on specified event frequency
						c.add(Calendar.WEEK_OF_MONTH, recurFreq);
						nextOccurance = c.getTime();
						start = nextOccurance;
					}
				}
				if(temp==recurNum) {
					endCalendar.add(Calendar.DATE,1);
					end=endCalendar.getTime();
					start=end;
					recurNum = increment;
					break;
				}
				end = EventScheduleUtil.dateFormat(jpObj.get(END_DATE),END);
				break;
				
			case 'm':
				// If number of occurrences is 0 i.e. end date is 2099-12-31, get number of occurrences between start and end
				if(EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get(RECUR_NUM)),0)==0) {
					startCalendar.setTime(start);
					endCalendar.setTime(end);
					
					diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
					
					recurNum = ((diffYear * 12) + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH))/recurFreq;
				}
				
				// Logic to handle event type months
				c.setTime(start);
				
				while(c.get(Calendar.MONTH)!=month) {
					c.add(Calendar.MONTH,1);
				}
				
				for(int iCtr=0;iCtr<=recurNum;iCtr++) {
					
					currOccurance = c.getTime();
	
					jpObj.put(START_DATE, currOccurance);
					
					if(currOccurance.before(end) && month==c.get(Calendar.MONTH)) {
						scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);
		
						scheduleObj = schedulerMongo.save(scheduleObj);
						
						month=c.get(Calendar.MONTH)+recurFreq;
						if(month>11) month=month-12;
						c.add(Calendar.MONTH, recurFreq);
						temp+=1;
					} else {

						c.add(Calendar.MONTH, 1);
					}
					nextOccurance = c.getTime();
					start = nextOccurance;
					if(temp==recurNum) {
						endCalendar.add(Calendar.DATE,1);
						end=endCalendar.getTime();
						recurNum = increment;
						break;
					}
				}
				if(temp==recurNum) {
					endCalendar.add(Calendar.DATE,1);
					end=endCalendar.getTime();
					start=end;
					recurNum = increment;
					break;
				}
				end = EventScheduleUtil.dateFormat(jpObj.get(END_DATE),END);

				break;
				
			case 'y':
				// If number of occurrences is 0 i.e. end date is 2099-12-31, get number of occurrences between start and end
				if(EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get(RECUR_NUM)),0)==0) {
					startCalendar.setTime(start);
					endCalendar.setTime(end);
					
					diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
					
					recurNum = diffYear/recurFreq;
				}
				// Logic to handle event type year
				c.setTime(start);

				for(int iCtr=0;iCtr<recurNum;iCtr++) {
					currOccurance = c.getTime();
	
					jpObj.put("startDate", currOccurance);
					
					if(currOccurance.before(end)) {
						scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);
		
						scheduleObj = schedulerMongo.save(scheduleObj);
						c.add(Calendar.YEAR, recurFreq);
						temp+=1;
					}  else {

						c.add(Calendar.YEAR, 1);
					}
				}
				if(temp==recurNum) {
					endCalendar.add(Calendar.DATE,1);
					end=endCalendar.getTime();
					recurNum = increment;
					break;
				}
				end = EventScheduleUtil.dateFormat(jpObj.get(END_DATE),END);

				break;
		}

		return scheduleObj;
	}

	/**
	 * Method to purge all schedules from DB
	 * @return Returns a string to confirm schedules are purged
	 */
	@RequestMapping(value = "/purgeAllSchedules", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String purgeAllEvents() {

		schedulerMongo.deleteAll();
		return "Purged";
	}
}
