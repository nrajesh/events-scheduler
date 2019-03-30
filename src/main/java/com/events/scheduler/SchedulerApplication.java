package com.events.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class SchedulerApplication {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerApplication.class);
	
	/**
	 * The main run method
	 * @param args
	 */
	public static void main(String[] args) {	
		SpringApplication.run(SchedulerApplication.class, args);
		logger.debug("--Application Started--");
	}

}
