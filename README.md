# Event Scheduler

A simple recurring schedule library written in Java. Springboot and Mongo DB are utilized to quickly showcase a working demo of this utility

[![View Source][SOURCE-BADGE]](https://github.com/nrajesh/scheduler)
[![Download Source][EXE-BADGE]](https://raw.githubusercontent.com/nrajesh/events-scheduler/master/img/scheduler-0.1.0-exec.jar)
[![View Demo][DEMO-BADGE]](http://localhost:8080)
[![MIT License][LICENSE-BADGE]](LICENSE)

[SOURCE-BADGE]: https://img.shields.io/badge/view-source-brightgreen.svg
[DEMO-BADGE]: https://img.shields.io/badge/view-demo-brightgreen.svg
[LICENSE-BADGE]: https://img.shields.io/badge/license-MIT-blue.svg
[OPEN-FILE]: https://img.shields.io/badge/open-File-orange.svg
[EXE-BADGE]: https://img.shields.io/badge/EXE-Executable-orange.svg

## Contents

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
  * [Test via Multi-line Submit](#multi-submit)
  * [RESTful Requests](#restful-requests)
  * [JUnit Test Cases](#junit-test-cases)
* [License](#license)
* [Support](#support)

### Introduction

The utility of this library is to setup events and their corresponding schedules.

### Sample Use Cases

This library supports the following use cases:

* >Every second Saturday is a holiday
`{"eventName":"Holiday","recurPattern":"w","weekPattern":"sat","recurFreq":"2"}`

* >Remind me to pay my phone bill on the 10th of every month
`{"eventName":"Pay bill", "startDate":"2019-04-10","recurNum":"100","recurPattern":"m","startMonth":"apr","recurFreq":"1"}`

* >2nd Sep is my anniversary
`{"eventName":"Anniversary", "startDate":"2019-09-02","recurPattern":"y","recurFreq":"1"}`

* >Every Tuesday and Thursday is team catch-up
`{"eventName":"Team catch-up", "recurNum":"10","recurPattern":"w","weekPattern":"tue,thu","recurFreq":"1"}`

* >Every 1st and 3rd Sunday, I need to visit the hospital
`{"eventName":"Visit hospital", "recurNum":"10","recurPattern":"w","weekPattern":"sat","recurFreq":"2"}`

* >2nd Dec 2017 we have a school reunion (non-recurrent event)
`{"eventName":"School reunion", "startDate":"2017-12-02","recurNum":"1","recurPattern":"y","recurFreq":"1"}`

* >Every alternate Wednesday our sprint ends
`{"eventName":"Sprint ends", "recurNum":"10","recurPattern":"w","weekPattern":"wed","recurFreq":"2"}`

* >Once in 2 months, on the 10th I need to pay my credit card bill
`{"eventName":"Credit card bill", "startDate":"2019-04-10","recurNum":"10","recurPattern":"m","startMonth":"apr","recurFreq":"2"}`

* >Once in every quarter, 5th we have shareholders’ meeting
`{"eventName":"Shareholders meeting", "startDate":"2019-04-05","recurNum":"10","recurPattern":"m","startMonth":"apr","recurFreq":"3"}`

### Usage

1. Get started by grabbing the executable [![EXE-BADGE]](https://raw.githubusercontent.com/nrajesh/events-scheduler/master/scheduler-1.0.0-exec.jar)
2. Then open the [localhost URL](http://localhost:8080)
3. The resultant data is fetched from and saved to a Mongo DB backend. The properties of this database can be adjusted in application.properties file [![OPEN-FILE]](https://github.com/nrajesh/scheduler/blob/master/src/main/resources/application.properties)

![Default Screen](https://raw.githubusercontent.com/nrajesh/events-scheduler/master/img/defaultScreen.png)

#### Search

The section at the top has a drop-down to:

* Search schedules by **exact** event name and occurance date (from date).

  * If no date is mentioned all schedule occurances from today will be displayed.

  * If occurance count is marked as 0, all event occurances will be displayed, else only first 'n' recurrances will show up.

* Obtain count of recurrances of an event by specifying an **exact** event name.

* Search results are displayed between search and entry sections (one schedule in each line)

#### Single Request

The left side of the screen is meant to submit one event at a time. There are no mandatory fields by design, although it will be good to have an event name entered (for looking up schedules via search).

Start and end dates represent the event boundary dates. If left blank, they default to today and an arbitrary end date (31st Dec 2099) respectively.

Either an end date or recurrence count number is needed. If both are entered, recurrence count takes precedence. If end date is left blank & recurrence count is left as 0, the event end date will be capped to the above mentioned arbitrary end date.

Submit will save the event object and corresponding schedules to DB. By design, event names can be duplicated and there are no duplicate checks.

#### Multi-line Request

The multi-line request makes it possible to schedule multiple events at any given time. It accepts following parameters (dates in yyyy-MM-dd format):

* *eventName*: Name of the event
* *startDate*: Start date of event
* *endDate*: End date of event
* *recurNum*: Max. count of scheduled recurrences of the event
* *recurPattern*: This can take a value of _'d' or 'w' or 'm' or 'y'_ to represent adaily, weekly, monthly or yearly pattern in the schedule
* *weekPattern*: The first 3 characters of weekday in lower case. Defaults to '_mon_'
* *startMonth*: The first 3 characters of month name in lower case. Defaults to '_jan_'
* *recurFreq*: The frequency of event recurrence mentioned as a number. Ex.: fortnightly=2, once a quarter=3

#### Fetch and Purge

The three buttons at the bottom of the page make it possible to:

* Fetch all events
* Purge all events
* Purge all schedules

Please note that there are no confirmations asked before purging! All deletions are permanent.

### Code

That was the design. Here is a look at the underlying code and some of the Spring annotations used in this project.

* The _main()_ method in SchedulerApplication class is the starting point of the application.

* This initial class also includes _SpringBootApplication_ & _EnableAutoConfiguration_ annotations of SpringBoot. These convenience annotations automatically configure the Spring application using the included jar files, based on dependencies specified in the pom.xml

* The _SocketConfig_ is registered as a Configuration object. The included _EnableWebSocketMessageBroker_ annotation enables broker-backed messaging over WebSocket. On page load, the fnConnect method registers the _Stomp Client_ object over which JS requests are submitted.

* User interactions are captured as _FORM_ submit actions. The flow of code then shifts to the controller objects that are registered via the _Controller_ annotation for the specific user action represented by _RequestMapping_ or _MessageMapping_ annotation value.

* Wherever it is necessary to call another action on completion of the current one, the _SendTo_ annotation is employed. The same action name is also registered to the stomp client's _subscribe_ method (in the front end).

#### Stack

At the core, the _Events Scheduler_ uses _Java 1.8_, _HTML5/ JavaScript_ for **front-end** and _Spring Boot_ as the **backend** REST API. _Maven_ is used as the **build tool**. A MongoDB database is used for the **storage layer**.

#### Patterns

1. _MVC_: The code is decoupled as model, view and controller. The models events and schedule represent objects carrying data that get eventually stored in their respective MongoDB Collections. The objects themselves are loosely coupled. Each schedule object has an event id field that can optionally link it to an event object. The main eventing and scheduling logic is written in the EventController and ScheduleController classes. The view as well as front-end validations are managed via HTML5/ JS.

2. _Facade Pattern_: The interfaces that extend MongoRepository, that providies simplified methods required to manage CRUD operations on the DB layer, delegate the calls to respective DTO objects. These DTOs have concrete implementations that are utilized by controller classes when making DB calls.

3. _Filter Pattern_: The schedule objects that are fetched as part of the search operation, get filtered based on the event object parameters used in the search criteria, like event name and occurrence date.

4. _Composite Pattern_: We have the Schedule object which acts as a Composite Pattern class. This class contains groups of events i.e. schedules contain an event id that could be used to group its own objects.

### Testing

Events Scheduler library can be tested in following ways:

#### Multi-Submit

The method of testing via multiple JSON object submission on the front end remains the easiest way to test the functionality of the events scheduler. This is detailed in the [Multi-line Request](#multi-line-request) section.

#### RESTful Requests

The core functionalities of the event scheduler i.e. fetch, insert and purge (of both event and schedules) can be accessed via REST API calls done over POST requests. Fetch and insertion actions may require an input payload that is managed via raw messages in request body that are structured in JSON format

> _/saveSingleEvent_: This API call makes it possible to save a single event object. This requires an input payload as shown below (one row for each parameter)

![Save Single Event](https://raw.githubusercontent.com/nrajesh/events-scheduler/master/img/saveSingleEvent.png)

> The resulting JSON object represents a single event object

![Save Single Event DB](https://raw.githubusercontent.com/nrajesh/events-scheduler/master/img/saveSingleEventDB.png)

> _/insertSchedule_: This API call makes it possible to insert multiple schedule objects for any given event. This requires an input payload as shown below (one row for each parameter)

![Insert Schedule](https://raw.githubusercontent.com/nrajesh/events-scheduler/master/img/insertSchedule.png)

> The resulting JSON object represents schedule objects

![Insert Schedule DB](https://raw.githubusercontent.com/nrajesh/events-scheduler/master/img/insertScheduleDB.png)

> _/fetchSchedules_: This API call makes it possible to fetch schedule objects given an event name and schedule start date in input payload.

![Fetch Schedules](https://raw.githubusercontent.com/nrajesh/events-scheduler/master/img/fetchSchedules.png)

> _/fetchScheduleCount_: This API call makes it possible to count the number of occurrences of any given event name (in input payload).

![Fetch Schedule Count](https://raw.githubusercontent.com/nrajesh/events-scheduler/master/img/fetchScheduleCount.png)

> _purgeAllSchedules_/ _purgeAllEvents_: These API calls are essentially a shortcut to purge the DB of all schedule/ event records. At the moment, both these will simply do the data deletion and return a string "Purged", so errors are possibly noticed while doing JSON.parse on the output data stream.

#### JUnit Test Cases

A sample JUnit test case is covered in the package named _com.events.scheduler.test_. The _ScheduleMongoRepositoryTest_ class contains the pre-test setup, test implementation and post-test annotations to do a DB purge.

### License

This is free and open source software. You can use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of it, under the terms of the MIT License.

This software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or implied. See the MIT License for details.

### Support

To report bugs, suggest improvements, or ask questions, please visit the [Issues Tracker](https://github.com/nrajesh/events-scheduler/issues)
