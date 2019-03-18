package com.calendar.scheduler.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

	private Date start, end;
	
	@Autowired
	private IScheduleMongoDB schedulerMongo;

	@RequestMapping(value = "/fetchAllSchedules", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public List<ScheduleObj> fetchAllEvents() {
		return schedulerMongo.findAll();
	}

	@MessageMapping(value = "/insertSchedule")
	@ResponseBody
	public ScheduleObj setupEvent(@RequestBody String selectedData) {
		JSONParser jp = new JSONParser(selectedData.toString());
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	    
		ScheduleObj scheduleObj = new ScheduleObj();
		int increment = 0,recurFreq = 0;
		try {
			Map<String, Object> jpObj = jp.object();
			recurFreq = EventScheduleUtil.intFormat(String.valueOf((java.math.BigInteger) jpObj.get("recurFreq")));

			start = EventScheduleUtil.dateFormat(jpObj.get("startDate"), "start");
			end = EventScheduleUtil.dateFormat(jpObj.get("endDate"), "end");

			if (recurFreq == 0 || !format.parse("2099-12-30").equals(end)) {
				while (end.after(start)) {
					scheduleObj = insertScheduleRecords(scheduleObj, jpObj, increment);
					increment++;
				}
			} else {
				while (increment != recurFreq) {
					scheduleObj = insertScheduleRecords(scheduleObj, jpObj, increment);
					increment++;
				}
			}

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

	private ScheduleObj insertScheduleRecords(ScheduleObj scheduleObj, Map<String, Object> jpObj, int increment) {

		int recurNum = EventScheduleUtil.intFormat(String.valueOf((java.math.BigInteger) jpObj.get("recurNum")));
		Calendar c = Calendar.getInstance();
		char recurPattern = EventScheduleUtil.charFormat(jpObj.get("recurPattern"));
		switch (recurPattern) {
			case 'd':
				c.setTime(start);
				c.add(Calendar.DATE, recurNum);
				start = c.getTime();
				jpObj.put("startDate", start);
		}
		scheduleObj = EventScheduleUtil.createScheduleObj(jpObj);

		scheduleObj = schedulerMongo.save(scheduleObj);

		return scheduleObj;
	}

	@RequestMapping(value = "/purgeAllSchedules", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String purgeAllEvents() {

		schedulerMongo.deleteAll();
		return "Purged";
	}
}
