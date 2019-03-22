var jsonObj;
var stompClient = null;
var stompConnected = false;

function showNxtPattern() {
    var selPattern = document.getElementById('recurPattern').value;
    document.getElementById('repeatPattern').style.visibility="visible";
    if(selPattern == 'w') {
        document.getElementById('weekPattern').hidden=false;
        document.getElementById('monthPattern').hidden=true;
        document.getElementById('repeatPattern').hidden=false;
        document.getElementById('dummy').hidden=true;
    } else if(selPattern == 'm') {
        document.getElementById('weekPattern').hidden=true;
        document.getElementById('monthPattern').hidden=false;
        document.getElementById('repeatPattern').hidden=false;
        document.getElementById('dummy').hidden=true;
    } else if(selPattern == 'y') {
        document.getElementById('weekPattern').hidden=true;
        document.getElementById('monthPattern').hidden=true;
        document.getElementById('repeatPattern').hidden=true;
        document.getElementById('dummy').hidden=false;
    }
}

function fnAlterSearchType() {
    var selPattern = document.getElementById('searchType').value;
    document.getElementById('scheduleDtls').style.visibility="visible";
    document.getElementById('scheduleCount').style.visibility="visible";
    
    if(selPattern == 'fetchOccurances') {
        document.getElementById('scheduleDtls').hidden = false;
        document.getElementById('scheduleCount').hidden = true;
    } else if(selPattern == 'countOccurances') {
        document.getElementById('scheduleDtls').hidden = true;
        document.getElementById('scheduleCount').hidden = false;
    }
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
    
    if (document.getElementById('weekPattern').hidden)
        document.getElementById('weekPattern').hidden=true;
    if(document.getElementById('monthPattern').hidden)
        document.getElementById('monthPattern').hidden=true;
}

function fnInsertSchedule(jsonObj) {
    stompClient.send("/insertSchedule", {}, jsonObj);
}

function fnConnect() {
    var socket = new SockJS('/websocket');
    
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompConnected = true;
        stompClient.subscribe('/topic/saveSingleEvent',function(message) {
            fnInsertSchedule(message.body);
        });
        stompClient.subscribe('/topic/fetchScheduleCount',function(message) {
            $("ul#search > li").remove();

            $("#search").append('<li>Found:' + message.body + ' Occurance(s)</li>');
        });
        stompClient.subscribe('/topic/fetchSchedules',function(message) {
            
            $("ul#search > li").remove();
            var msgBody = JSON.parse(message.body);
            var length = msgBody.length;
            
            for(var i=0;i<length;i++) {
                $("#search").append('<li>' + msgBody[i].occuranceDate + '</li>');
            }

            if(length==0) {
                $("#search").append('<li>No results</li>');
            }
        });

    });
}

function fnDisconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
        stompConnected = false;
    }
    console.log("Disconnected");
}