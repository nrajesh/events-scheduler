var jsonObj;
var stompClient = null;
var weekVal = [{"key":"Mon","val":"Monday"},{"key":"Tue","val":"Tuesday"}];

function showNxtPattern() {
    var selPattern = document.getElementById('recurPattern').value;
    document.getElementById('repeatPattern').style.visibility="visible";
    if(selPattern == 'weeks') {
        document.getElementById('weekPattern').hidden=false;
        document.getElementById('monthPattern').hidden=true;
        document.getElementById('repeatPattern').hidden=false;
    } else if(selPattern == 'months') {
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

function fnSubmit() {
    var weekPattern = document.getElementById('weekPattern');
    var monthPattern = document.getElementById('monthPattern');

    var weekValues = [];
    for (var i = 0; i < weekPattern.options.length; i++) {
        if (weekPattern.options[i].selected) {
            weekValues.push(weekPattern.options[i].value);
        }
    }
    var monthValues = [];
    for (var i = 0; i < monthPattern.options.length; i++) {
        if (monthPattern.options[i].selected) {
            monthValues.push(monthPattern.options[i].value);
        }
    }
    jsonObj = 
        '{'
        +'"eventName":"'+document.getElementById('eventName').value+'",'
        +'"startDate":"'+document.getElementById('startDate').value+'",'
        +'"endDate":"'+document.getElementById('endDate').value+'",'
        +'"recurNum":"'+document.getElementById('recurNum').value+'",'
        +'"recurPattern":"'+document.getElementById('recurPattern').value+'",'
        +'"weekPattern":"'+weekValues+'",'
        +'"monthPattern":"'+monthValues+'",'
        +'"recurFreq":"'+document.getElementById('recurFreq').value+'"'
        +'}';

    //var selectedData = JSON.parse(jsonObj);
    stompClient.send("/saveSingleEvent", {}, jsonObj);

    fnDisconnect();
    fnConnect();
    document.getElementById('weekPattern').hidden=true;
    document.getElementById('monthPattern').hidden=true;
    document.getElementById('repeatPattern').hidden=true;
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
    });
}

function fnDisconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}