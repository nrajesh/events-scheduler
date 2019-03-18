package com.calendar.scheduler.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.calendar.scheduler.dto.IEventsMongoDB;
import com.calendar.scheduler.model.EventObj;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventsMongoRepositoryTest {
    @Autowired
    private IEventsMongoDB schedulerMongo;
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    
    @Before
    public void setUp() throws Exception {
        EventObj testEvent1= new EventObj(
        		"Holiday",
        		format.parse("2019-03-25"),
        		format.parse("2019-03-26"),
        		2,
        		"months",
        		"",
        		"1",
        		1);
        EventObj testEvent2= new EventObj(
        		"Meeting",
        		format.parse("2019-04-25"),
        		format.parse("2019-05-26"),
        		1,
        		"weeks",
        		"2",
        		"",
        		2);
        //save event, verify it has ID value after save
        assertNull(testEvent1.getId());
        assertNull(testEvent2.getId());

        this.schedulerMongo.save(testEvent1);
        this.schedulerMongo.save(testEvent2);
        assertNotNull(testEvent1.getId());
        assertNotNull(testEvent1.getId());
    }

    @Test
    public void testFetchData(){
        /*Test data retrieval*/
    	EventObj event1;
		try {
			event1 = schedulerMongo.findByStartDate(format.parse("2019-03-25"));
	        assertNotNull(event1);
	        assertEquals(format.parse("2019-03-26"), event1.getEndDate());
	        /*Get all products, list should only have two*/
	        Iterable<EventObj> events = schedulerMongo.findAll();
	        int count = 0;
	        for(EventObj p : events){
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
        	EventObj event2 = schedulerMongo.findByStartDate(format.parse("2019-04-25"));
        	assertNotNull(event2);
			event2.setEndDate(format.parse("2019-04-26"));
	        schedulerMongo.save(event2);
	        
	        EventObj event3= schedulerMongo.findByStartDate(format.parse("2019-04-25"));
	        assertNotNull(event3);
	        assertEquals(format.parse("2019-04-26"), event3.getEndDate());
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
