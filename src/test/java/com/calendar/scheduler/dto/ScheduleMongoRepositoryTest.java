package com.calendar.scheduler.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.calendar.scheduler.SchedulerApplication;
import com.calendar.scheduler.model.EventObj;
import com.calendar.scheduler.model.ScheduleObj;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleMongoRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerApplication.class);
	
    @Autowired
    private IScheduleMongoDB schedulerMongo;
    
    @Autowired
    private IEventsMongoDB eventMongo;
    
    @Before
    public void setUp() {
		logger.debug("Executing test setup");
    	
    	LocalDate startDate = LocalDate.now();
    	startDate.atStartOfDay(ZoneId.of("UTC"));
    	
//    	set(2099,11,31);
//    	LocalDate endDate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));

    	LocalDate endDate = startDate.plusDays(1);
    	endDate.atStartOfDay(ZoneId.of("UTC"));
    	
    	EventObj evtObj1 = new EventObj(
        		"Holiday",
        		startDate, 
        		endDate,
        		0,
        		'w',
        		"Sat",
        		0,
        		2);
        //save event, verify it has ID value before save
        assertNull(evtObj1.getId());
        this.eventMongo.save(evtObj1);
        //Verify if saved event has ID value after save
        assertNotNull(evtObj1.getId());
        
        ScheduleObj scheduleObj1 = new ScheduleObj(
        		evtObj1.getId(),
        		evtObj1.getEventName(),
        		startDate
        		);
        //save schedule, verify it has ID value before save
        assertNull(scheduleObj1.getId());
        this.schedulerMongo.save(scheduleObj1);
        //Verify if saved schedule has ID value after save
        assertNotNull(scheduleObj1.getId());
		logger.debug("evtObj1: "+ evtObj1.getEventName()+" ~ Starts on: "+evtObj1.getStartDate());
    }

    @Test
    public void testFetchData() {
		logger.debug("Executing testFetchData");

    	LocalDate occurrenceDate = LocalDate.now();
    	occurrenceDate.atStartOfDay(ZoneId.of("UTC"));
    	
        /*Test data retrieval*/

		logger.debug("Occurrence date for fetch is: "+ occurrenceDate);
		ScheduleObj schedule1 = this.schedulerMongo.findByOccurrenceDate(occurrenceDate);
        assertNotNull(schedule1);
		logger.debug("Found schedule is: "+schedule1.getEventName());
        assertEquals(occurrenceDate, schedule1.getOccurrenceDate());
    }
    
    @Test
    public void testDataUpdate(){
		logger.debug("Executing testDataUpdate");
    	
    	LocalDate occurrenceDate = LocalDate.now();
    	occurrenceDate.atStartOfDay(ZoneId.of("UTC"));
    	
        /*Test update*/
	    
		ScheduleObj schedule2 = schedulerMongo.findByOccurrenceDate(occurrenceDate);
    	assertNotNull(schedule2);
		logger.debug("Old schedule2 date is: "+schedule2.getOccurrenceDate());

    	occurrenceDate = occurrenceDate.plusDays(1);
    	occurrenceDate.atStartOfDay(ZoneId.of("UTC"));
    	schedule2.setOccurrenceDate(occurrenceDate);
		logger.debug("New schedule2 date is: "+occurrenceDate);
        schedulerMongo.save(schedule2);
    }
    
    @After
    public void tearDown() throws Exception {
    	this.eventMongo.deleteAll();
    	this.schedulerMongo.deleteAll();
    }
}
