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

@Controller
public class ScheduleController {
	public class HomeController {

		@RequestMapping(value = "/")
		public String index() {
			return "index.html";
		}
	}

	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	private Date start, end;
	Date currOccurance,nextOccurance;
	
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

			cntOccurances = EventScheduleUtil.intFormat(jpObj.get("numSchedules"),1);
			
			for(ScheduleObj schObj : fetchAllSchedules()) {
				if(!"".equals(EventScheduleUtil.eliminateNull((String)jpObj.get("srchEvtName")))
						&& (schObj.getEventName().equals(EventScheduleUtil.eliminateNull((String)jpObj.get("srchEvtName"))))) {

					if(!schObj.getOccuranceDate().before(EventScheduleUtil.dateFormat(jpObj.get("srchStartDate"),"start"))
							&& cntOccurances==0) {
						rsltLst.add(schObj);
					} else if(cntr < cntOccurances && 
							!schObj.getOccuranceDate().before(EventScheduleUtil.dateFormat(jpObj.get("srchStartDate"),"start"))) {
						rsltLst.add(schObj);
					}
				} else if("".equals(EventScheduleUtil.eliminateNull((String)jpObj.get("srchEvtName")))) {
					if(!schObj.getOccuranceDate().before(EventScheduleUtil.dateFormat(jpObj.get("srchStartDate"),"start"))
							&& cntOccurances==0) {
						rsltLst.add(schObj);
					} else if(cntr < cntOccurances && 
							!schObj.getOccuranceDate().before(EventScheduleUtil.dateFormat(jpObj.get("srchStartDate"),"start"))) {
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
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
	    
		ScheduleObj scheduleObj = new ScheduleObj();
		int increment = 0,recurNum = 0;
		try {
			Map<String, Object> jpObj = jp.object();
			recurNum = EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get("recurNum")),1);

			start = EventScheduleUtil.dateFormat(jpObj.get("startDate"), "start");
			end = EventScheduleUtil.dateFormat(jpObj.get("endDate"), "end");

			if (recurNum == 0 || !format.parse("2099-12-31").equals(end)) {
				while (end.after(start) || end.equals(start)) {
					scheduleObj = insertScheduleRecords(scheduleObj, jpObj, recurNum, increment);
					increment++;
				}
			} else {
				while (increment != recurNum) {
					scheduleObj = insertScheduleRecords(scheduleObj, jpObj, recurNum, increment);
					increment++;
				}
			}
			currOccurance = nextOccurance = null;

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

		int recurFreq = EventScheduleUtil.intFormat(String.valueOf((BigInteger) jpObj.get("recurFreq")),1);
		
		Calendar c = Calendar.getInstance();
		
		int diff=0,temp=0;
		
		char recurPattern = EventScheduleUtil.charFormat(jpObj.get("recurPattern"));
		
		int[] weekDays = EventScheduleUtil.getWeekDays((String)jpObj.get("weekPattern"));
		int month = EventScheduleUtil.intFormat(String.valueOf((BigInteger)jpObj.get("monthPattern")),0);

		if(null==nextOccurance) {
			c.setTime(start);
		} else {
			c.setTime(nextOccurance);
		}
		switch (recurPattern) {
			case 'd':
					
				currOccurance = c.getTime();
				jpObj.put("startDate", currOccurance);
				scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);

				scheduleObj = schedulerMongo.save(scheduleObj);
				c.add(Calendar.DATE, recurFreq);
				nextOccurance = c.getTime();
				start = nextOccurance;
				
				break;
				
			case 'w':
				
				c.add(Calendar.DATE, 7*recurFreq);
				nextOccurance = c.getTime();
				
				if(null!=currOccurance && currOccurance.after(end) && nextOccurance.after(end)) {
					start = nextOccurance;
					break;
				}
				c.setTime(start);
				
				for(int weekDay: weekDays) {
					temp = c.get(Calendar.DAY_OF_WEEK);
					diff = weekDay-temp;
					if(diff<0) {
						c.add(Calendar.DATE, diff+7);
					} else {
						c.add(Calendar.DATE, diff);
					}
					currOccurance = c.getTime();
					jpObj.put("startDate", currOccurance);
					
					scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);

					scheduleObj = schedulerMongo.save(scheduleObj);
					//nextOccurance = c.getTime();
					start = nextOccurance;
				}

				break;
				
			case 'm':

				c.set(c.get(Calendar.YEAR),month+(recurFreq*increment),c.get(Calendar.DATE));
				
				currOccurance = c.getTime();

				jpObj.put("startDate", currOccurance);
				
				scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);

				scheduleObj = schedulerMongo.save(scheduleObj);

				//c.add(Calendar.MONTH, recurFreq);

				start = currOccurance;

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
