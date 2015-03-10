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

function loadMap() {
    var directionsDisplay = new google.maps.DirectionsRenderer();
    var ffwMoe = new google.maps.LatLng(49.334283, 8.851337);
    var mapOptions = {
        zoom: 10,
        center: ffwMoe,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    
    var map = new google.maps.Map(document.getElementById("map"), mapOptions);
    directionsDisplay.setMap(map);
    
    var lat  = $("#latitude").text();
    var lng  = $("#longitude").text();
    var dest = new google.maps.LatLng(lat, lng);
    var request = {
        origin: ffwMoe,
        destination: dest,
        travelMode: google.maps.TravelMode.DRIVING,
        unitSystem: google.maps.UnitSystem.METRIC
    };
    
    var directionsService = new google.maps.DirectionsService();
    directionsService.route(request, function(result, status) {
        if (status == google.maps.DirectionsStatus.OK) {
            directionsDisplay.setDirections(result);
            
            var totalDistance = 0;
            var totalDuration = 0;
            var legs = result.routes[0].legs;
            for(var i=0; i<legs.length; ++i) {
                totalDistance += legs[i].distance.text;
                totalDuration += legs[i].duration.text;
            }
            
            $("#distance").text(" " + totalDistance);
            $("#duration").text("~" + totalDuration);
        }
    });
}