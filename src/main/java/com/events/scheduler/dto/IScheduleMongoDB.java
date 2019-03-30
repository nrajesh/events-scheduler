package com.events.scheduler.dto;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.events.scheduler.model.ScheduleObj;
 
public interface IScheduleMongoDB extends MongoRepository<ScheduleObj, Integer> {
	ScheduleObj findByEventName(String eventName);
	ScheduleObj findByOccurrenceDate(LocalDate occurrenceDate);
	void deleteAll();
}