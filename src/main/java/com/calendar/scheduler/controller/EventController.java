package com.calendar.scheduler.controller;

import java.util.List;
import java.util.Map;

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
import com.calendar.scheduler.dto.IEventsMongoDB;
import com.calendar.scheduler.model.EventObj;
import com.calendar.scheduler.util.EventScheduleUtil;
import com.calendar.scheduler.util.SchedulerConstants;

@Controller
public class EventController implements SchedulerConstants {
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
	private static final Logger logger = LoggerFactory.getLogger(SchedulerApplication.class);
	
    /**
     * Autowired annotation to resolve and inject IEventsMongoDB interface
     */
    @Autowired
    private IEventsMongoDB eventMongo;
    
	/**
	 * Method to fetch all events
	 * @return List of @EventObj
	 */
	@RequestMapping(value="/fetchAllEvents", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public List<EventObj> fetchAllEvents() {
		logger.debug("Executing fetchAllEvents");
		
		return eventMongo.findAll();
	}
	
	/**
	 * Method to save a single event that gets invoked on receiving the /saveSingleEvent message
	 * @param A single @EventObj with null id
	 * @return @EventObj with id populated after save is conveyed to the topic URL used as @SendTo destination for the reply
	 */
	@MessageMapping(value="/saveSingleEvent")
	@ResponseBody
	@SendTo(value="/topic/saveSingleEvent")
	public EventObj setupEvent(@RequestBody String selectedData) {
		logger.debug("Input for saveSingleEvent: "+selectedData);
		
		JSONParser jp = new JSONParser(selectedData);
		
		EventObj eventObj = new EventObj();
		try {
			Map<String,Object> jpObj=jp.object();
			// Make an event object
			eventObj = EventScheduleUtil.createEventObj(jpObj);

		    eventObj = eventMongo.save(eventObj);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug("Result of saveSingleEvent: "+eventObj.toString());
		return eventObj;
	}
	
	/**
	 * Method to purge all events from DB
	 * @return Returns a string to confirm events are purged
	 */
	@RequestMapping(value="/purgeAllEvents", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String purgeAllEvents() {
		logger.debug("Entered purgeAllEvents");

		eventMongo.deleteAll();
		
		logger.debug("Exited purgeAllEvents");
		return "Purged";
	}
}
