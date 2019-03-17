package com.calendar.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.calendar.scheduler.dto.ISchedulerMongoDB;

@Controller
public class PurgeAllEvents {

    @Autowired
    private ISchedulerMongoDB schedulerMongo;
	
	@MessageMapping(value="/schedule/purgeAllEvents")
	public String purgeAllEvents() {

	    this.schedulerMongo.deleteAll();
		return "";
	}
}
