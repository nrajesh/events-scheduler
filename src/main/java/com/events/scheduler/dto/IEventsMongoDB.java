package com.events.scheduler.dto;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.events.scheduler.model.EventObj;
 
public interface IEventsMongoDB extends MongoRepository<EventObj, Integer> {
	EventObj findByEventName(String eventName);
	void deleteAll();
	EventObj findByStartDate(LocalDate startDate);
}