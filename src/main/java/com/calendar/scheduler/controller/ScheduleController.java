package com.calendar.scheduler.controller;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
	public class HomeController {

		@RequestMapping(value = "/")
		public String index() {
			return "index.html";
		}
	}

	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	private Date start, end, currOccurance,nextOccurance;
	private int tempCntr=0;
	
	@Autowired
	private IScheduleMongoDB schedulerMongo;

	@RequestMapping(value = "/fetchAllSchedules", produces = "application/json; charset=UTF-8")
	@ResponseBody
	private List<ScheduleObj> fetchAllSchedules() {
		return schedulerMongo.findAll();
	}
	
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

			cntOccurances = EventScheduleUtil.intFormat(jpObj.get(NUM_SCHEDULES),1);
			Date startDate = EventScheduleUtil.dateFormat(jpObj.get(SEARCH_START_DATE),START);
			String eventName = EventScheduleUtil.eliminateNull((String)jpObj.get(SEARCH_EVENT_NAME));
			
			for(ScheduleObj schObj : fetchAllSchedules()) {
				if(!EMPTY_STRING.equals(eventName)
						&& (schObj.getEventName().equals(eventName))) {

					if(!schObj.getOccuranceDate().before(startDate)
							&& cntOccurances==0) {
						rsltLst.add(schObj);
					} else if(cntr < cntOccurances && 
							!schObj.getOccuranceDate().before(startDate)) {
						rsltLst.add(schObj);
					}
				} else if(EMPTY_STRING.equals(eventName)) {
					if(cntr <= cntOccurances || !schObj.getOccuranceDate().before(startDate)) {
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
				
				if(schObj.getEventName().contains((String)jpObj.get("eventName"))) {
					result++;
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	@MessageMapping(value = "/insertSchedule")
	@ResponseBody
	public ScheduleObj setupEvent(@RequestBody String selectedData) {
		JSONParser jp = new JSONParser(selectedData.toString());
		format.setTimeZone(TimeZone.getTimeZone(DEFAULT_TZ));
	    
		ScheduleObj scheduleObj = new ScheduleObj();
		int increment = 0,recurNum = 0;
		try {
			Map<String, Object> jpObj = jp.object();
			recurNum = EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get("recurNum")),1);

			start = EventScheduleUtil.dateFormat(jpObj.get("startDate"), "start");
			end = EventScheduleUtil.dateFormat(jpObj.get("endDate"), "end");

			if(recurNum == 0 && !format.parse(END_DATE_LONG).before(end)) {
				recurNum++;
			}
			if (recurNum == 1) {
				//while (end.after(start) || end.equals(start)) {
				if(!format.parse(END_DATE_LONG).before(end)) {
					tempCntr=0;
					 scheduleObj = insertScheduleRecords(scheduleObj, jpObj, recurNum, increment);
						increment++;
				} 
			} else {
				while (increment != recurNum) {
					if(!format.parse(END_DATE_LONG).before(end)) {
						scheduleObj = insertScheduleRecords(scheduleObj, jpObj, recurNum, increment);
						increment++;
					} else {
						break;
					}
				}
			}
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

	private ScheduleObj insertScheduleRecords(ScheduleObj scheduleObj, Map<String, Object> jpObj, int recurNum, int increment) {

		int recurFreq = EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get(RECUR_FREQ)),1);
		
		Calendar c = Calendar.getInstance();
		
		//String evtName = EventScheduleUtil.eliminateNull((String)jpObj.get(EVENT_NAME));
		
		char recurPattern = EventScheduleUtil.charFormat(jpObj.get(RECUR_PATTERN));
		
		int[] weekDays = EventScheduleUtil.getWeekDays((String)jpObj.get(WEEK_PATTERN));
		int month = EventScheduleUtil.intFormat(String.valueOf((BigInteger)jpObj.get(START_MONTH)),0);

		if(null==nextOccurance) {
			c.setTime(start);
		} else {
			c.setTime(nextOccurance);
		}
		switch (recurPattern) {
			case 'd':
					
				currOccurance = c.getTime();
				jpObj.put(START_DATE, currOccurance);
				scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);

				scheduleObj = schedulerMongo.save(scheduleObj);
				end = (Date)jpObj.get("startDate");
				c.add(Calendar.DATE, recurFreq);
				nextOccurance = c.getTime();
				start = nextOccurance;
				
				break;
				
			case 'w':
				
				if(tempCntr==0) {
					for(int weekDay: weekDays) {

						c.setTime(start);
						/*temp = c.get(Calendar.DAY_OF_WEEK);
						while(temp!=weekDay) {
							c.add(Calendar.DATE, 1);
							temp = c.get(Calendar.DAY_OF_WEEK);
						}*/
	
						for(int cntr=0;cntr<recurNum;cntr++) {
							currOccurance = c.getTime();
							jpObj.put("startDate", currOccurance);
							
							if(currOccurance.before(end) && weekDay==c.get(Calendar.DAY_OF_WEEK)) {
								scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);
			
								scheduleObj = schedulerMongo.save(scheduleObj);
							} else {
								c.add(Calendar.WEEK_OF_MONTH, recurFreq);

								break;
							}
		
							c.add(Calendar.WEEK_OF_MONTH, recurFreq);
						}
					}
					tempCntr++;
					break;
				}
				break;
				
			case 'm':

				c.setTime(start);
				c.set(c.get(Calendar.YEAR),month+(recurFreq*increment),c.get(Calendar.DATE));
				
				currOccurance = c.getTime();
				
				if(new Date().after(currOccurance)) {
					c.add(Calendar.YEAR,1);
					currOccurance = c.getTime();
				} 

				jpObj.put("startDate", currOccurance);
				
				scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);

				scheduleObj = schedulerMongo.save(scheduleObj);

				start = currOccurance;
				end = (Date)jpObj.get("startDate");

				break;
				
			case 'y':

				c.set(c.get(Calendar.YEAR)+(recurFreq*increment),c.get(Calendar.MONTH),c.get(Calendar.DATE));
				
				currOccurance = c.getTime();

				jpObj.put("startDate", currOccurance);
				
				scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);

				scheduleObj = schedulerMongo.save(scheduleObj);

				//c.add(Calendar.YEAR, recurFreq);

				start = currOccurance;

				break;
		}

		return scheduleObj;
	}

	@RequestMapping(value = "/purgeAllSchedules", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String purgeAllEvents() {

		schedulerMongo.deleteAll();
		return "Purged";
	}
}
