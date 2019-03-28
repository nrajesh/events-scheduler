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

import com.calendar.scheduler.model.EventObj;
import com.calendar.scheduler.model.ScheduleObj;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleMongoRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(ScheduleMongoRepositoryTest.class);
	
    @Autowired
    private IScheduleMongoDB schedulerMongo;
    
    @Autowired
    private IEventsMongoDB eventMongo;
    
    @Before
    public void setUp() {
		logger.debug("Executing test setup");
    	
    	LocalDate startDate = LocalDate.now();
    	startDate.atStartOfDay(ZoneId.of("UTC"));

    	LocalDate endDate = startDate.plusDays(1);
    	endDate.atStartOfDay(ZoneId.of("UTC"));
    	
    	//Every second Saturday is a holiday
    	EventObj evtObj1 = new EventObj(
        		"Holiday",
        		startDate, 
        		endDate,
        		0,
        		'w',
        		"Sat",
        		0,
        		2);

		setupEvtObj(evtObj1);
    	
    	//Every Tuesday and Thursday is team catch-up
    	EventObj evtObj2 = new EventObj(
        		"Team catch-up",
        		startDate, 
        		endDate,
        		0,
        		'w',
        		"Tue,Thu",
        		0,
        		1);
    	
		setupEvtObj(evtObj2);
		logger.debug("Exiting test setup");
    }

    private void setupEvtObj(EventObj evtObj) {
		logger.debug("Executing setupEvtObj");
    	
        //save event, verify it has ID value before save
        assertNull(evtObj.getId());
        this.eventMongo.save(evtObj);
        
        //Verify if saved event has ID value after save
        assertNotNull(evtObj.getId());
        
        ScheduleObj scheduleObj1 = new ScheduleObj(
        		evtObj.getId(),
        		evtObj.getEventName(),
        		evtObj.getStartDate()
        		);
        
        //save schedule, verify it has ID value before save
        assertNull(scheduleObj1.getId());
        this.schedulerMongo.save(scheduleObj1);
        
        //Verify if saved schedule has ID value after save
        assertNotNull(scheduleObj1.getId());

		logger.debug("evtObj1: "+ evtObj.getEventName()+" ~ Starts on: "+evtObj.getStartDate());
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
