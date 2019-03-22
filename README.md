# Event Scheduler

A simple recurring schedule library written in Java. Springboot and Mongo DB are utilized to quickly showcase a working demo of this utility

[![View Source][SOURCE-BADGE]](https://github.com/nrajesh/scheduler)
[![View Demo][DEMO-BADGE]](http://localhost:8080)
[![MIT License][LICENSE-BADGE]](LICENSE)

[SOURCE-BADGE]: https://img.shields.io/badge/view-source-brightgreen.svg
[DEMO-BADGE]: https://img.shields.io/badge/view-demo-brightgreen.svg
[LICENSE-BADGE]: https://img.shields.io/badge/license-MIT-blue.svg
[OPEN-FILE]: https://img.shields.io/badge/open-File-orange.svg

Contents
--------
* [Introduction](#introduction)
* [Sample Use Cases](#sample-use-cases)
* [Get Started](#get-started)
* [The Code](#the-code)


Introduction
------------
The utility of this library is to setup a simple set of events and their corresponding schedules.


Sample Use Cases
-----------------
This library supports the following use cases:

>Every second Saturday is a holiday

>Remind me to pay my phone bill on the 10th of every month

>2nd Sep is my anniversary

>Every Tuesday and Thursday is team catch-up

>Every 1st and 3rd Sunday, I need to visit the hospital

>2nd Dec 2017 we have a school reunion (non-recurrent event)

>Every alternate Wednesday our sprint ends

>Once in 2 months, on the 10th I need to pay my credit card bill

>Once in every quarter, 5th we have shareholders’ meeting


Get Started
------------
Get started by running the executable JAR file. Then open the demo URL [![Demo URL][DEMO-BADGE]](http://localhost:8080). The resultant data is fetched from and saved to a Mongo DB backend. The properties of this database can be adjusted in application.properties file [![Open File][OPEN-FILE]](https://github.com/nrajesh/scheduler/blob/master/src/main/resources/application.properties)

The Code
---------
