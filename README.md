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
  * [Single Request](#single-request)
  * [Multi-line Request](#multi-line-request)
  * [SOAP Request](#soap-request)
* [Code](#code)
  * [Stack](#stack)
  * [Patterns](#patterns)


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
1. Get started by grabbing the executable [![EXE-BADGE]](https://storage.googleapis.com/n-r_scheduler_project/scheduler-0.1.0-exec.jar).
2. Then open the localhost URL [![[DEMO-BADGE]](http://localhost:8080).

Single Request
--------------

The resultant data is fetched from and saved to a Mongo DB backend. The properties of this database can be adjusted in application.properties file [![Open File][OPEN-FILE]](https://github.com/nrajesh/scheduler/blob/master/src/main/resources/application.properties)

Code
=========
