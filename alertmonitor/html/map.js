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

// old coordinates: 
// var originLat = 49.334283;
// var originLng = 8.851337;
var originLat = 49.3341297;
var originLng = 8.8509882;



function loadMap() {
  var mapProvider = $("#mapProvider").text().trim();
  
  if (mapProvider == "googlemaps") {
    loadGoogleMaps();
  } else if (mapProvider == "openstreetmap") {
    loadOpenStreetMap();
  }
}

function loadGoogleMaps() {
  var directionsDisplay = new google.maps.DirectionsRenderer();
  var ffwMoe = new google.maps.LatLng(originLat, originLng);

  var mapOptions = {
    zoom: 10,
    center: ffwMoe,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  };
  
  var map = new google.maps.Map(document.getElementById("map"), mapOptions);
  directionsDisplay.setMap(map);
  
  var lat  = $("#latitude").text();
  var lng  = $("#longitude").text();
  var street  = $("#street").text();
  var village = $("#village").text();
  var dest;

  if (lat != "" && lng != "") {
    dest = new google.maps.LatLng(lat, lng);
  } else {
    dest = street + ", " + village;
  }

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

function loadOpenStreetMap() {
  alert("TODO: openstreetmap");
}



/*
var map;
var layer_mapnik;
var layer_tah;
var layer_markers;


function loadOpenStreetMap() {

  
  OpenLayers.Lang.setCode('de');
  
  // Position und Zoomstufe der Karte

  

  map = new OpenLayers.Map('map', {
    projection: new OpenLayers.Projection("EPSG:900913"),
    displayProjection: new OpenLayers.Projection("EPSG:4326"),
    controls: [
      new OpenLayers.Control.Navigation(),
      new OpenLayers.Control.LayerSwitcher(),
      new OpenLayers.Control.PanZoomBar()
    ],
    maxExtent:
      new OpenLayers.Bounds(-20037508.34,-20037508.34,
                             20037508.34, 20037508.34),
    numZoomLevels: 18,
    maxResolution: 156543,
    units: 'meters'
  });



  layer_mapnik = new OpenLayers.Layer.OSM.Mapnik("Mapnik");
  layer_markers = new OpenLayers.Layer.Markers("Address", { projection: new OpenLayers.Projection("EPSG:4326"), 
  	                                          visibility: true, displayInLayerSwitcher: false });

  map.addLayers([layer_mapnik, layer_markers]);

  map.addLayer(new OpenLayers.Layer.OSM("Fire hydrants","http://openfiremap.org/hytiles/${z}/${x}/${y}.png",
	                                  { numZoomLevels: 18, transitionEffect: 'resize', alpha: true, isBaseLayer: false }));
  
  var zoom = 15;
  jumpTo(originLng, originLat, zoom);

  

  var popuptext="<font color=\"black\"><b>Thomas Heiles<br>Stra&szlig;e 123<br>54290 Trier</b><p><img src=\"test.jpg\" width=\"180\" height=\"113\"></p></font>";


  // Position des Markers
  var destLat = $("#latitude").text();
  var destLng = $("#longitude").text();
  addMarker(layer_markers, destLat, destLng, popuptext);
}


function jumpTo(lon, lat, zoom) {
    var x = Lon2Merc(lon);
    var y = Lat2Merc(lat);
    map.setCenter(new OpenLayers.LonLat(x, y), zoom);
    return false;
}
 
function Lon2Merc(lon) {
    return 20037508.34 * lon / 180;
}
 
function Lat2Merc(lat) {
    var PI = 3.14159265358979323846;
    lat = Math.log(Math.tan( (90 + lat) * PI / 360)) / (PI / 180);
    return 20037508.34 * lat / 180;
}
 
function addMarker(layer, lon, lat, popupContentHTML) {
 
    var ll = new OpenLayers.LonLat(Lon2Merc(lon), Lat2Merc(lat));
    var feature = new OpenLayers.Feature(layer, ll); 
    feature.closeBox = true;
    feature.popupClass = OpenLayers.Class(OpenLayers.Popup.FramedCloud, {minSize: new OpenLayers.Size(300, 180) } );
    feature.data.popupContentHTML = popupContentHTML;
    feature.data.overflow = "hidden";
 
    var marker = new OpenLayers.Marker(ll);
    marker.feature = feature;
 
    var markerClick = function(evt) {
        if (this.popup == null) {
            this.popup = this.createPopup(this.closeBox);
            map.addPopup(this.popup);
            this.popup.show();
        } else {
            this.popup.toggle();
        }
        OpenLayers.Event.stop(evt);
    };
    marker.events.register("mousedown", feature, markerClick);
 
    layer.addMarker(marker);
    map.addPopup(feature.createPopup(feature.closeBox));
}
 
function getCycleTileURL(bounds) {
   var res = this.map.getResolution();
   var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
   var y = Math.round((this.maxExtent.top - bounds.top) / (res * this.tileSize.h));
   var z = this.map.getZoom();
   var limit = Math.pow(2, z);
 
   if (y < 0 || y >= limit)
   {
     return null;
   }
   else
   {
     x = ((x % limit) + limit) % limit;
 
     return this.url + z + "/" + x + "/" + y + "." + this.type;
   }
}


*/

