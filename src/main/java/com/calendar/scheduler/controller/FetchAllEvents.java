package com.calendar.scheduler.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.calendar.scheduler.dto.ISchedulerMongoDB;
import com.calendar.scheduler.model.EventObj;

@Controller
public class FetchAllEvents {

    @Autowired
    private ISchedulerMongoDB schedulerMongo;
	
	@MessageMapping(value="/schedule/fetchAllEvents")
	public List<EventObj> fetchAllEvents() {
	    
		return this.schedulerMongo.findAll();
	}
}
