package com.calendar.scheduler.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.calendar.scheduler.model.ScheduleObj;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleMongoRepositoryTest {
    @Autowired
    private IScheduleMongoDB schedulerMongo;
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    
    @Before
    public void setUp() throws Exception {
    	format.setTimeZone(TimeZone.getTimeZone("GMT"));
	    
        ScheduleObj testSchedule1= new ScheduleObj(
        		"1",
        		"Holiday",
        		format.parse("2019-03-25"));
        ScheduleObj testSchedule2= new ScheduleObj(
        		"2",
        		"Meeting",
        		format.parse("2019-04-25"));
        //save event, verify it has ID value after save
        assertNull(testSchedule1.getId());
        assertNull(testSchedule2.getId());

        this.schedulerMongo.save(testSchedule1);
        this.schedulerMongo.save(testSchedule2);
        assertNotNull(testSchedule1.getId());
        assertNotNull(testSchedule2.getId());
    }

    @Test
    public void testFetchData(){
        /*Test data retrieval*/
    	ScheduleObj schedule1;
		try {
			format.setTimeZone(TimeZone.getTimeZone("GMT"));
		    
			schedule1 = schedulerMongo.findByOccuranceDate(format.parse("2019-03-25"));
	        assertNotNull(schedule1);
	        assertEquals(format.parse("2019-03-25"), schedule1.getOccuranceDate());
	        /*Get all products, list should only have two*/
	        Iterable<ScheduleObj> schedule = schedulerMongo.findAll();
	        int count = 0;
	        for(ScheduleObj p : schedule){
	            count++;
	        }
	        assertEquals(count, 2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void testDataUpdate(){
        /*Test update*/
    	try {
    		format.setTimeZone(TimeZone.getTimeZone("GMT"));
    	    
    		ScheduleObj schedule2 = schedulerMongo.findByOccuranceDate(format.parse("2019-04-25"));
        	assertNotNull(schedule2);
        	schedule2.setOccuranceDate(format.parse("2019-04-26"));
	        schedulerMongo.save(schedule2);
	        
	        ScheduleObj schedule3= schedulerMongo.findByOccuranceDate(format.parse("2019-04-26"));
	        assertNotNull(schedule3);
	        assertEquals(format.parse("2019-04-26"), schedule3.getOccuranceDate());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @After
    public void tearDown() throws Exception {
      this.schedulerMongo.deleteAll();
    }
}
