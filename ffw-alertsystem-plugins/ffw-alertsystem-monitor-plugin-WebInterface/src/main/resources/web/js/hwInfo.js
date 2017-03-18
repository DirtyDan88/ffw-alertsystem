var hwInfoWS  = new WebSocket("ws://" + url + "/sockets/hwinfo/");

hwInfoWS.onopen = function () {
  $('#conHwInfoWs').html('HwInfoWebSocket <img src=\"images/ws-open.png\" title=\"connected\" />');
};

hwInfoWS.onerror = function (error) {
  console.log('hwInfoWS error ' + error);
  $('#conHwInfoWs').html('HwInfoWebSocket <img src=\"images/ws-closed.png\" title=\"error' + error + '\" />');
};

hwInfoWS.onmessage = function (e) {
  $('#hwInfo').html(e.data);
};

hwInfoWS.onclose = function() { 
  console.log('hwInfoWS closed');
  $('#conHwInfoWs').html('HwInfoWebSocket <img src=\"images/ws-closed.png\" title=\"closed\" />');
};
