package com.calendar.scheduler.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
public class EventObj {

	@Id
	private String id;
	
	private String eventName;
	private LocalDate startDate;
	private LocalDate endDate;
	private int recurNum;
	private char recurPattern;
	private String weekPattern;
	private int startMonth;
	private int recurFreq;
	
    public EventObj(
    		String eventName,
    		LocalDate startDate, 
    		LocalDate endDate,
    		int recurNum,
    		char recurPattern,
    		String weekPattern,
    		int startMonth,
    		int recurFreq) {
    	
    	this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recurNum = recurNum;
        this.recurPattern = recurPattern;
        this.weekPattern = weekPattern;
        this.startMonth = startMonth;
        this.recurFreq = recurFreq;
    }
	
	public EventObj() {
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
	 * @return the startDate
	 */
	public LocalDate getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public LocalDate getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the recurNum
	 */
	public int getRecurNum() {
		return recurNum;
	}
	/**
	 * @param recurNum the recurNum to set
	 */
	public void setRecurNum(int recurNum) {
		this.recurNum = recurNum;
	}
	/**
	 * @return the recurPattern
	 */
	public char getRecurPattern() {
		return recurPattern;
	}
	/**
	 * @param recurPattern the recurPattern to set
	 */
	public void setRecurPattern(char recurPattern) {
		this.recurPattern = recurPattern;
	}
	/**
	 * @return the weekPattern
	 */
	public String getWeekPattern() {
		return weekPattern;
	}
	/**
	 * @param weekPattern the weekPattern to set
	 */
	public void setWeekPattern(String weekPattern) {
		this.weekPattern = weekPattern;
	}
	/**
	 * @return the startMonth
	 */
	public int getstartMonth() {
		return startMonth;
	}
	/**
	 * @param weekPattern the weekPattern to set
	 */
	public void setstartMonth(int startMonth) {
		this.startMonth = startMonth;
	}
	/**
	 * @return the recurFreq
	 */
	public int getRecurFreq() {
		return recurFreq;
	}
	/**
	 * @param recurFreq the recurFreq to set
	 */
	public void setRecurFreq(int recurFreq) {
		this.recurFreq = recurFreq;
	}
	
    @Override
    public String toString() {
    	StringBuffer strVal = new StringBuffer();
    	strVal.append("Event:");
    	strVal.append("id=");
    	strVal.append(this.id);
    	strVal.append("\n");
    	strVal.append("eventName=");
    	strVal.append(this.eventName);
    	strVal.append("\n");
    	strVal.append("startDate=");
    	strVal.append(this.startDate);
    	strVal.append("\n");
    	strVal.append("endDate=");
    	strVal.append(this.endDate);
    	strVal.append("\n");
    	strVal.append("recurNum=");
    	strVal.append(this.recurNum);
    	strVal.append("\n");
    	strVal.append("recurPattern=");
    	strVal.append(this.recurPattern);
    	strVal.append("\n");
    	strVal.append("weekPattern=");
    	strVal.append(this.weekPattern);
    	strVal.append("\n");
    	strVal.append("recurFreq=");
    	strVal.append(this.recurFreq);
    	strVal.append("\n");
        return strVal.toString();
    }
}
