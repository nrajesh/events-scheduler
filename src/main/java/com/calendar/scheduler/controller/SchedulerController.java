package com.calendar.scheduler.controller;

import java.util.Map;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	@MessageMapping(value="/schedule/setupEvent")
	public String setupEvent(@RequestBody String selectedData) {
		JSONParser jp = new JSONParser(selectedData);
		
		EventObj eventObj;
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
		}
		return selectedData;
	}
}
