INSERT INTO AlertMessage (
  timestamp, 
  address, 
  function, 
  isComplete, 
  isEncrypted, 
  isTestAlert, 
  hasCoordinates, 
  latitude, 
  longitude, 
  street, 
  village,
  alertNumber, 
  shortKeyword, 
  alertLevel, 
  messageString
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
