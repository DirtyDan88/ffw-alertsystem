var pluginWS  = new WebSocket("ws://" + url + "/sockets/plugins/");

pluginWS.onopen = function () {
  $('#conPluginWs').html('PluginWebSocket <img src=\"images/ws-open.png\" title=\"connected\" />');
};

pluginWS.onerror = function (error) {
  console.log('pluginWS error ' + error);
  $('#conPluginWs').html('PluginWebSocket <img src=\"images/ws-closed.png\" title=\"error' + error + '\" />');
};

pluginWS.onmessage = function (e) {
  var message = e.data;

  if (message.substring(0, 4) == 'log:') {
    console.log('server: ' + message.substring(5));
  } else {
    $('#list-plugins').html(e.data);
  }
};

pluginWS.onclose = function() {
  console.log('pluginWS closed');
  $('#conPluginWs').html('PluginWebSocket <img src=\"images/ws-closed.png\" title=\"closed\" />');
};





function pluginActivate(instanceName) {
  console.log('activate plugin: ' + instanceName);
  httpGetAsync("/api/plugins/activate/" + instanceName);
}

function pluginDeactivate(instanceName) {
  console.log('deactivate plugin: ' + instanceName);
  httpGetAsync("/api/plugins/deactivate/" + instanceName);
}

function pluginRestart(instanceName) {
  console.log('restart plugin: ' + instanceName);
  httpGetAsync("/api/plugins/restart/" + instanceName);
}

function httpGetAsync(uri) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
      console.log("response from GET " + uri + ": " + xmlHttp.responseText);
//        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
 //           callback(xmlHttp.responseText);
    }
    xmlHttp.open("GET", uri, true); // true for asynchronous
    xmlHttp.send(null);
}
