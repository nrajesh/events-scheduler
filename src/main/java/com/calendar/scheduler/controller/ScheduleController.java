package com.calendar.scheduler.controller;

import java.util.List;
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
    @Autowired
    private IScheduleMongoDB schedulerMongo;
    
	@RequestMapping(value="/fetchAllSchedules", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public List<ScheduleObj> fetchAllEvents() {
		return schedulerMongo.findAll();
	}
	
	@MessageMapping(value="/insertSchedule")
	@ResponseBody
	public ScheduleObj setupEvent(@RequestBody String selectedData) {
		JSONParser jp = new JSONParser(selectedData.toString());
		
		ScheduleObj scheduleObj = new ScheduleObj();
		EventScheduleUtil scheduleUtil = new EventScheduleUtil();
		try {
			Map<String,Object> jpObj=jp.object();
			scheduleObj = scheduleUtil.createScheduleObj(jpObj);

			scheduleObj = schedulerMongo.save(scheduleObj);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scheduleObj;
	}
	
	@RequestMapping(value="/purgeAllSchedule", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String purgeAllEvents() {

		schedulerMongo.deleteAll();
		return "Purged";
	}
}
