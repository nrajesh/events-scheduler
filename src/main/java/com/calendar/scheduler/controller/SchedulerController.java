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

import com.calendar.scheduler.dto.ISchedulerMongoDB;
import com.calendar.scheduler.model.EventObj;
import com.calendar.scheduler.util.EventSchedulerUtil;

@Controller
public class SchedulerController {
	public class HomeController {
	 
	    @RequestMapping(value = "/")
	    public String index() {
	        return "index.html";
	    }
	}
    @Autowired
    private ISchedulerMongoDB schedulerMongo;
    
	@RequestMapping(value="/fetchAllEvents", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public List<EventObj> fetchAllEvents() {
		return this.schedulerMongo.findAll();
	}
	
	@MessageMapping(value="/saveSingleEvent")
	@ResponseBody
	public EventObj setupEvent(@RequestBody String selectedData) {
		JSONParser jp = new JSONParser(selectedData);
		
		EventObj eventObj = new EventObj();
		EventSchedulerUtil eventUtil = new EventSchedulerUtil();
		try {
			Map<String,Object> jpObj=jp.object();

			eventObj = new EventObj(
				(String)jpObj.get("eventName"),
				eventUtil.dateFormat(jpObj.get("startDate")),
				eventUtil.dateFormat(jpObj.get("endDate")),
				eventUtil.intFormat(jpObj.get("recurNum")),
				(String)jpObj.get("recurPattern"),
				(String)jpObj.get("weekPattern"),
				(String)jpObj.get("monthPattern"),
				eventUtil.intFormat(jpObj.get("recurFreq"))
			);

		    eventObj = this.schedulerMongo.save(eventObj);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return eventObj;
	}
	
	@RequestMapping(value="/purgeAllEvents", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String purgeAllEvents() {

		schedulerMongo.deleteAll();
		return "Purged";
	}
}
