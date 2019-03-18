var jsonObj;
var stompClient = null;
var weekVal = [{"key":"Mon","val":"Monday"},{"key":"Tue","val":"Tuesday"}];

function showNxtPattern() {
    var selPattern = document.getElementById('recurPattern').value;
    document.getElementById('repeatPattern').style.visibility="visible";
    if(selPattern == 'weekly') {
        document.getElementById('weekPattern').hidden=false;
        document.getElementById('monthPattern').hidden=true;
        document.getElementById('repeatPattern').hidden=false;
    } else if(selPattern == 'monthly') {
        document.getElementById('weekPattern').hidden=true;
        document.getElementById('monthPattern').hidden=false;
        document.getElementById('repeatPattern').hidden=false;
    }
}

function fnFetch() {
    
    stompClient.send("/fetchAllEvents", {}, "");

    document.getElementById('weekPattern').hidden=true;
    document.getElementById('monthPattern').hidden=true;
    document.getElementById('repeatPattern').hidden=true;

    fnDisconnect();
    fnConnect();
}

function fnTypePattern(pattern) {

    var patternVal = [];
    if(undefined==pattern) {
        return "";
    }
    for (var i = 0; i < pattern.options.length; i++) {
        if (pattern.options[i].selected) {
            patternVal.push(pattern.options[i].value);
        }
    }

    return patternVal;
}

function fnSubmit(eventObj) {
    var jsonObj = 
        '{'
        +'"eventName":"'+eventObj.eventName+'",'
        +'"startDate":"'+eventObj.startDate+'",'
        +'"endDate":"'+eventObj.endDate+'",'
        +'"recurNum":"'+eventObj.recurNum+'",'
        +'"recurPattern":"'+eventObj.recurPattern+'",'
        +'"weekPattern":"'+eventObj.weekPattern+'",'
        +'"monthPattern":"'+eventObj.monthPattern+'",'
        +'"recurFreq":"'+eventObj.recurFreq+'"'
        +'}';

    stompClient.send("/saveSingleEvent", {}, jsonObj);
    
    document.getElementById('weekPattern').hidden=true;
    document.getElementById('monthPattern').hidden=true;
    document.getElementById('repeatPattern').hidden=true;
}

function fnInsertSchedule(jsonObj) {
    stompClient.send("/insertSchedule", {}, jsonObj);
}

function fnPurge() {
    
    stompClient.send("/purgeAllEvents", {}, "");

    document.getElementById('weekPattern').hidden=true;
    document.getElementById('monthPattern').hidden=true;
    document.getElementById('repeatPattern').hidden=true;
}

function fnConnect() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/saveSingleEvent',function(message) {
            fnInsertSchedule(message.body);
        });

    });
}

function fnDisconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}