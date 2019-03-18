package com.calendar.scheduler.dto;

import com.calendar.scheduler.model.EventObj;

import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;
 
public interface IEventsMongoDB extends MongoRepository<EventObj, Integer> {
	EventObj findByEventName(String eventName);
	EventObj findByStartDate(Date startDate);
	void deleteAll();
}