package com.calendar.scheduler.dto;

import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.calendar.scheduler.model.EventObj;
import com.calendar.scheduler.model.ScheduleObj;
 
public interface IScheduleMongoDB extends MongoRepository<ScheduleObj, Integer> {
	ScheduleObj findByEventName(String eventName);
	ScheduleObj findByOccuranceDate(Date occuranceDate);
	void deleteAll();
}