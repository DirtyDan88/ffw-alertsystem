/*
    Copyright (c) 2015, Max Stark <max.stark88@web.de> 
        All rights reserved.
    
    This file is part of ffw-alertsystem, which is free software: you 
    can redistribute it and/or modify it under the terms of the GNU 
    General Public License as published by the Free Software Foundation, 
    either version 2 of the License, or (at your option) any later 
    version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details. 
    
    You should have received a copy of the GNU General Public License 
    along with this program; if not, see <http://www.gnu.org/licenses/>.
*/

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