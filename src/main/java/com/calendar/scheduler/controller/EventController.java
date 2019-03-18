package com.calendar.scheduler.controller;

import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.calendar.scheduler.dto.IEventsMongoDB;
import com.calendar.scheduler.model.EventObj;
import com.calendar.scheduler.util.EventScheduleUtil;

@Controller
public class EventController {
	public class HomeController {
	 
	    @RequestMapping(value = "/")
	    public String index() {
	        return "index.html";
	    }
	}
    @Autowired
    private IEventsMongoDB eventMongo;
    
	@RequestMapping(value="/fetchAllEvents", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public List<EventObj> fetchAllEvents() {
		return eventMongo.findAll();
	}
	
	@MessageMapping(value="/saveSingleEvent")
	@ResponseBody
	@SendTo(value="/topic/saveSingleEvent")
	public EventObj setupEvent(@RequestBody String selectedData) {
		JSONParser jp = new JSONParser(selectedData);
		
		EventObj eventObj = new EventObj();
		try {
			Map<String,Object> jpObj=jp.object();
			eventObj = EventScheduleUtil.createEventObj(jpObj);

		    eventObj = eventMongo.save(eventObj);
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

		eventMongo.deleteAll();
		return "Purged";
	}
}
