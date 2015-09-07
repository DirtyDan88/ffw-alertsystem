CREATE TABLE IF NOT EXISTS `AlertMessage` (
  `ID`             INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,

  `timestamp`      TEXT NOT NULL,
  `address`        TEXT NOT NULL,
  `function`       TEXT NOT NULL,
  
  `isComplete`         INTEGER NOT NULL,
  `isEncrypted`        INTEGER NOT NULL,
  `isTestAlert`        INTEGER NOT NULL,
  `isFireAlert`        INTEGER NOT NULL,
  `unknownMessageType` INTEGER NOT NULL,

  `alertNumber`    TEXT,
  `alertSymbol`    TEXT,
  `alertLevel`     TEXT,
  `alertKeyword`   TEXT,

  `hasCoordinates`   INTEGER NOT NULL,
  `latitude`         TEXT,
  `longitude`        TEXT,
  `street`           TEXT,
  `village`          TEXT,
  `furtherPlaceDesc` TEXT,

  `keywords` TEXT,
  `messageString`  TEXT
);
