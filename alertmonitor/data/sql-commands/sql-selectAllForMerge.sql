SELECT 
  timestamp, 
  address, 
  function,

  isComplete, 
  isEncrypted, 
  isTestAlert, 
  isFireAlert,
  unknownMessageType,

  alertNumber,
  alertSymbol,  
  alertLevel,
  alertKeyword, 
  
  hasCoordinates, 
  latitude, 
  longitude, 
  street, 
  village,
  furtherPlaceDesc, 

  keywords,
  messageString
 FROM AlertMessage;
