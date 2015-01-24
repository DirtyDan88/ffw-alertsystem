function updateTimer() {
    var alertTime = $("#timestamp").text();
    var curTime   = new Date().getTime() / 1000;
    
    var diff = (curTime - alertTime);
    diff = Math.round(diff);
    /* diff is in seconds */
    var hours = Math.floor(diff / 3600);
    var mins  = Math.floor((diff % 3600) / 60);
    var secs  = Math.floor(diff % 60);
    
    var tmp = ""; 
    var formattedTime = "";
    tmp = "0" + hours.toString();
    tmp = tmp.substr(tmp.length-2);
    formattedTime += tmp + "h"
    tmp = "0" + mins.toString();
    tmp = tmp.substr(tmp.length-2);
    formattedTime += " " + tmp + "m"
    tmp = "0" + secs.toString();
    tmp = tmp.substr(tmp.length-2);
    formattedTime += " " + tmp + "s"
    
    $("#timeSinceAlert").text(formattedTime);
    window.setTimeout("updateTimer()", 1000);
}

function setAlertTime() {
    var date = new Date($("#timestamp").text() * 1000);
    
    var year = date.getFullYear();
    var month = date.getMonth();
    var dayInMonth = date.getDate();
    var dayInWeek = date.getDay();
    var hours = "0" + date.getHours();
    var mins = "0" + date.getMinutes();
    var secs = "0" + date.getSeconds();
    
    var dayNames   = new Array("Sonntag", "Montag", "Dienstag", "Mittwoch", 
                                "Donnerstag", "Freitag", "Samstag");
    var monthNames = new Array("Jan", "Feb", "März", "April", "Mai", "Juni", 
                                "Juli", "Aug", "Sep", "Okt", "Nov", "Dez");

    var formattedTime = dayNames[dayInWeek] + ", " + 
                        dayInMonth + ". " + 
                        monthNames[month] + ". " + 
                        year + " um " + 
                        hours.substr(hours.length-2) + ':' +
                        mins. substr(mins.length-2) + ':' + 
                        secs. substr(secs.length-2) + " Uhr";
    
    
    $("#alertTime").text(formattedTime);
}