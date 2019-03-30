# Event Scheduler

A simple recurring schedule library written in Java. Springboot and Mongo DB are utilized to quickly showcase a working demo of this utility

[![View Source][SOURCE-BADGE]](https://github.com/nrajesh/scheduler)
[![Download Source][EXE-BADGE]](https://storage.googleapis.com/n-r_scheduler_project/scheduler-0.1.0-exec.jar)
[![View Demo][DEMO-BADGE]](http://localhost:8080)
[![MIT License][LICENSE-BADGE]](LICENSE)

[SOURCE-BADGE]: https://img.shields.io/badge/view-source-brightgreen.svg
[DEMO-BADGE]: https://img.shields.io/badge/view-demo-brightgreen.svg
[LICENSE-BADGE]: https://img.shields.io/badge/license-MIT-blue.svg
[OPEN-FILE]: https://img.shields.io/badge/open-File-orange.svg
[EXE-BADGE]: https://img.shields.io/badge/EXE-Executable-orange.svg

Contents
========
* [Introduction](#introduction)
* [Sample Use Cases](#sample-use-cases)
* [Usage](#usage)
  * [UI Layout](#ui-layout)
  * [Search](#search)
  * [Single Request](#single-request)
  * [Multi-line Request](#multi-line-request)
  * [Fetch and Purge](#fetch-and-purge)
* [Code](#code)
  * [Stack](#stack)
  * [Patterns](#patterns)
* [Testing](#testing)
  * [RESTful Requests](#restful-requests)


Introduction
============
The utility of this library is to setup events and their corresponding schedules.


Sample Use Cases
================
This library supports the following use cases:

>Every second Saturday is a holiday
`{"eventName":"Holiday","recurPattern":"w","weekPattern":"sat","recurFreq":"2"}`

>Remind me to pay my phone bill on the 10th of every month
`{"eventName":"Pay bill", "startDate":"2019-04-10","recurNum":"100","recurPattern":"m","startMonth":"apr","recurFreq":"1"}`

>2nd Sep is my anniversary
`{"eventName":"Anniversary", "startDate":"2019-09-02","recurPattern":"y","recurFreq":"1"}`

>Every Tuesday and Thursday is team catch-up
`{"eventName":"Team catch-up", "recurNum":"10","recurPattern":"w","weekPattern":"tue,thu","recurFreq":"1"}`

>Every 1st and 3rd Sunday, I need to visit the hospital
`{"eventName":"Visit hospital", "recurNum":"10","recurPattern":"w","weekPattern":"sat","recurFreq":"2"}`

>2nd Dec 2017 we have a school reunion (non-recurrent event)
`{"eventName":"School reunion", "startDate":"2017-12-02","recurNum":"1","recurPattern":"y","recurFreq":"1"}`

>Every alternate Wednesday our sprint ends
`{"eventName":"Sprint ends", "recurNum":"10","recurPattern":"w","weekPattern":"wed","recurFreq":"2"}`

>Once in 2 months, on the 10th I need to pay my credit card bill
`{"eventName":"Credit card bill", "startDate":"2019-04-10","recurNum":"10","recurPattern":"m","startMonth":"apr","recurFreq":"2"}`

>Once in every quarter, 5th we have shareholders’ meeting
`{"eventName":"Shareholders meeting", "startDate":"2019-04-05","recurNum":"10","recurPattern":"m","startMonth":"apr","recurFreq":"3"}`

Usage
======
1. Get started by grabbing the executable [![EXE-BADGE]](https://storage.googleapis.com/n-r_scheduler_project/scheduler-0.1.0-exec.jar)
2. Then open the [localhost URL](http://localhost:8080)
3. The resultant data is fetched from and saved to a Mongo DB backend. The properties of this database can be adjusted in application.properties file [![OPEN-FILE]](https://github.com/nrajesh/scheduler/blob/master/src/main/resources/application.properties)

![Default Screen](https://storage.googleapis.com/n-r_scheduler_project/defaultScreen.png)

Search
-------
The section at the top has a drop-down to:
* Search schedules by **exact** event name and occurance date (from date).
    * If no date is mentioned all schedule occurances from today will be displayed.
    * If occurance count is marked as 0, all event occurances will be displayed, else only first 'n' recurrances will show up.
* Obtain count of recurrances of an event by specifying an **exact** event name.
* Search results are displayed between search and entry sections (one schedule in each line)

Single Request
--------------
The left side of the screen is meant to submit one event at a time. There are no mandatory fields by design, although it will be good to have an event name entered (for looking up schedules via search).

Start and end dates represent the event boundary dates. If left blank, they default to today and an arbitrary end date (31st Dec 2099) respectively.

Either an end date or recurrence count number is needed. If both are entered, recurrence count takes precedence. If end date is left blank & recurrence count is left as 0, the event end date will be capped to the above mentioned arbitrary end date.

Multi-line Request
------------------
The multi-line request makes it possible to schedule multiple events at any given time. It accepts following parameters (dates in yyyy-MM-dd format):
* *eventName*: Name of the event
* *startDate*: Start date of event
* *endDate*: End date of event
* *recurNum*: Max. count of scheduled recurrences of the event
* *recurPattern*: This can take a value of _'d' or 'w' or 'm' or 'y'_ to represent adaily, weekly, monthly or yearly pattern in the schedule
* *weekPattern*: The first 3 characters of weekday in lower case. Defaults to '_mon_'
* *startMonth*: The first 3 characters of month name in lower case. Defaults to '_jan_'
* *recurFreq*: The frequency of event recurrence mentioned as a number. Ex.: fortnightly=2, once a quarter=3

Fetch and Purge
---------------
The three buttons at the bottom of the page make it possible to:
* Fetch all events
* Purge all events
* Purge all schedules

Please note that there are no confirmations asked before purging! All deletions are permanent.

Code
=====
That was the design. Here is a look at the underlying code.

* The _main()_ method in SchedulerApplication class is the starting point of the application.

* This initial class also includes _SpringBootApplication_ & _EnableAutoConfiguration_ annotations of SpringBoot. These convenience annotations automatically configure the Spring application using the included jar files, based on dependencies in pom.xml

* 

Stack
------
At the core, the _Events Scheduler_ uses _Java 1.8_, _HTML5/ JavaScript_ for front end and _Spring Boot_ as the backend REST API. _Maven_ is used as the build tool. MongoDB is used as the storage layer.

Patterns
--------
_MVC_: The code is decoupled as model, view and controller. The models events and schedule represent objects carrying data that get eventually stored in their respective MongoDB Collections. The objects themselves are loosely coupled. Each schedule object has an event id field that can optionally link it to an event object. The main eventing and scheduling logic is written in the EventController and ScheduleController classes. All 

Testing
=======

RESTful Requests
----------------
The core functionalities of the event scheduler i.e. fetch, insert and purge (of both event and schedules) can be accessed via REST API calls done over POST requests. Fetch and insertion actions may require an input payload that is managed via raw messages in request body that are structured in JSON format

> _/saveSingleEvent_: This API call makes it possible to save a single event object. This requires an input payload as shown below (one row for each parameter)

![Save Single Event](https://storage.googleapis.com/n-r_scheduler_project/saveSingleEvent.png)

The resulting JSON object represents a single event object

![Save Single Event DB](https://storage.googleapis.com/n-r_scheduler_project/saveSingleEventDB.png)

> _/insertSchedule_: This API call makes it possible to insert multiple schedule objects for any given event. This requires an input payload as shown below (one row for each parameter)

![Insert Schedule](https://storage.googleapis.com/n-r_scheduler_project/insertSchedule.png)

The resulting JSON object represents schedule objects

![Insert Schedule DB](https://storage.googleapis.com/n-r_scheduler_project/insertScheduleDB.png)

> _/fetchSchedules_: This API call makes it possible to fetch schedule objects given an event name and schedule start date in input payload.

![Fetch Schedules](https://storage.googleapis.com/n-r_scheduler_project/fetchSchedules.png)

> _/fetchScheduleCount_: This API call makes it possible to count the number of occurrences of any given event name (in input payload).

![Fetch Schedule Count](https://storage.googleapis.com/n-r_scheduler_project/fetchScheduleCount.png)