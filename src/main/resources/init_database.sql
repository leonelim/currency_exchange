CREATE TABLE Currencies (
ID INTEGER PRIMARY KEY,
Code TEXT UNIQUE NOT NULL,
FullName TEXT NOT NULL,
Sign TEXT NOT NULL);


CREATE TABLE ExchangeRates (
ID INTEGER PRIMARY KEY,
BaseCurrencyId INTEGER,
TargetCurrencyId INTEGER,
Rate REAL,
UNIQUE (BaseCurrencyId, TargetCurrencyId),
FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID) ON DELETE CASCADE,
FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID) ON DELETE CASCADE,
CHECK (BaseCurrencyId != TargetCurrencyId));
