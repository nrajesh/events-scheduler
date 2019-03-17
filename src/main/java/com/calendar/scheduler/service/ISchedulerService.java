package com.calendar.scheduler.service;

import java.util.Date;

public interface ISchedulerService {

	public Date startDate();
	public Date endDate();
	public String getOccurrences(int limitNumberOfOccurences);
	public String getOccurrencesFrom(Date startDate, int numberOfOccurences);
	public String getAllOccurrences();
	public int getNumberOfOccurences();
}
