var actionWS = new WebSocket("ws://" + url + "/sockets/actions/");


actionWS.onopen = function () {
  $('#conActionWs').html('ActionWebSocket <img src=\"images/ws-open.png\" title=\"connected\" />');
};

actionWS.onerror = function (error) {
  console.log('actionWS error ' + error);
  $('#conActionWs').html('ActionWebSocket <img src=\"images/ws-closed.png\" title=\"error' + error + '\" />');
};

actionWS.onmessage = function (e) {
  $('#list-actions').html(e.data);
};

actionWS.onclose = function() {
  console.log('actionWS closed');
  $('#conActionWs').html('ActionWebSocket <img src=\"images/ws-closed.png\" title=\"closed\" />');
};


function actionActivate(actionName) {
  console.log('activate action: ' + actionName);
  httpGetAsync("/api/actions/activate/" + actionName);
}
function actionDeactivate(actionName) {
  console.log('deactivate action: ' + actionName);
  httpGetAsync("/api/actions/deactivate/" + actionName);
}
function actionReconnect(actionName) {
  console.log('reconnect action: ' + actionName);
  httpGetAsync("/api/actions/reconnect/" + actionName);
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
