var messageWS = new WebSocket("ws://" + url + "/sockets/messages/");

messageWS.onopen = function () {
  $('#conMessageWs').html('MessageWebSocket <img src=\"images/ws-open.png\" title=\"connected\" />');
};

messageWS.onerror = function (error) {
  console.log('messageWS error ' + error);
  $('#conMessageWs').html('MessageWebSocket <img src=\"images/ws-closed.png\" title=\"error' + error + '\" />');
};

messageWS.onmessage = function (e) {
  var message = e.data;

  $('#list-received-messages').html(message);
  $('.just-received').hide();
  $('.just-received').slideDown("slow", function() {
    removeEffectNewMessage($(this));
  });
};

function removeEffectNewMessage(elem) {
  setTimeout(function() {
    elem.removeClass('just-received');
  }, 4000);
}

messageWS.onclose = function() {
  console.log('messageWS closed');
  $('#conMessageWs').html('MessageWebSocket <img src=\"images/ws-closed.png\" title=\"closed\" />');
};


function toggleShowInvalid(checkbox) {
  if (checkbox.checked) {
    //$('.invalid').fadeIn();
    //hideInvalidMessages = false;
    messageWS.send("ALL_MESSAGES");
  } else {
    //$('.invalid').fadeOut();
    //hideInvalidMessages = true;
    messageWS.send("ONLY_VALID_MESSAGES");
  }
};


function sendMessage() {
  var message = buildMessage();

  if (message !== undefined) {
    showInfo(message);
    messageWS.send(message);
  }
}

function buildMessage() {
  // lat and long are optional
  var lat = $('#input-lat').val();
  var lng = $('#input-long').val();
  var latAndLng = '';
  if (lat != '' && lng != '') {
    latAndLng = lat + '/' + lng + '/';
  }

  var ric = $('#input-ric').val();
  if (ric == '') {
    showError($('#input-ric'));
    return;
  }

  var keyword = $('#input-keyword').val();
  if (keyword == '') {
    showError($('#input-keyword'));
    return;
  }

  var adress = $('#input-adress').val();
  if (adress == '') {
    showError($('#input-adress'));
    return;
  }

  var keywords = $('#input-keywords').val();
  if (keywords == '') {
    showError($('#input-keywords'));
    return;
  }

  var message = 'POCSAG1200: Address:  ' + ric +
                '  Function: 0  Alpha:   ' + latAndLng + '9999' + '/' +
                keyword + '//' + adress + '//' + keywords + '//';

  return message;
}

function showInfo(message) {
  $('#send-message-form-info').attr('class','form-success');
  $('#send-message-form-info').html('Nachricht gesendet:<br><b>' + message + '</b>');

  $('#send-message-form-info').fadeIn();
  setTimeout(hideInfoBox, 10000)
}

function showError(input) {
  $('#send-message-form-info').attr('class','form-error');
  $('#send-message-form-info').html(input.attr("placeholder") + ' fehlt');

  $('#send-message-form-info').fadeIn();
  setTimeout(hideInfoBox, 5000)
}

function hideInfoBox() {
  $('#send-message-form-info').fadeOut();
}
