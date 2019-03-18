package com.calendar.scheduler.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "schedule")
public class ScheduleObj {

	@Id
	private String id;

	private String eventId;
	private String eventName;
	private Date occuranceDate;
	
	public ScheduleObj(
		String eventId,
		String eventName,
		Date occuranceDate) {

        this.eventId = eventId;
    	this.eventName = eventName;
        this.occuranceDate = occuranceDate;
		
	}
	
	public ScheduleObj() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}
	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}
	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	/**
	 * @return the occuranceDate
	 */
	public Date getOccuranceDate() {
		return occuranceDate;
	}
	/**
	 * @param occuranceDate the occuranceDate to set
	 */
	public void setOccuranceDate(Date occuranceDate) {
		this.occuranceDate = occuranceDate;
	}
	

    @Override
    public String toString() {
    	StringBuffer strVal = new StringBuffer();
    	strVal.append("Schedule:");
    	strVal.append("id=");
    	strVal.append(this.id);
    	strVal.append("\n");
    	strVal.append("eventId=");
    	strVal.append(this.eventId);
    	strVal.append("\n");
    	strVal.append("eventName=");
    	strVal.append(this.eventName);
    	strVal.append("\n");
    	strVal.append("occuranceDate=");
    	strVal.append(this.occuranceDate);
    	strVal.append("\n");
        return strVal.toString();
    }
}
