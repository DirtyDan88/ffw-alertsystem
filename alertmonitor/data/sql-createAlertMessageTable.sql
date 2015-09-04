CREATE TABLE IF NOT EXISTS `AlertMessage` (
  `ID`             INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `timestamp`      TEXT NOT NULL,
  `address`        TEXT NOT NULL,
  `function`       TEXT NOT NULL,
  `isComplete`     INTEGER NOT NULL,
  `isEncrypted`    INTEGER NOT NULL,
  `isTestAlert`    INTEGER NOT NULL,
  `hasCoordinates` INTEGER NOT NULL,
  `latitude`       TEXT,
  `longitude`      TEXT,
  `street`         TEXT,
  `village`        TEXT,
  `alertNumber`    TEXT,
  `shortKeyword`   TEXT,
  `alertLevel`     TEXT,
  `messageString`  TEXT
);
