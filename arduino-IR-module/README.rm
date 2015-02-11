# Alarmmonitor_Feuerwehr
Einschaltroutine, für den Fernseher in der Fahrzeughalle

Dieses kleine Programm ermöglicht das Einschalten und Steuern eines Fernsehers, mittels Serialverbindung. Verwendet wurde ein "Arduino Mini Pro", an dem eine IR-LED angeschlossen ist.
Die Eingaben, die vom Fernseher erkannt werden, sind im Flash einprogrammiert und können über die UART-Schnittstelle nur angesprochen werden.

# Steuerung
Es werden drei Befehle erkannt, die den Fernseher Ein- (#AN) oder Ausschalten (#AU), oder den Status des Fernsehers zurückgeben (#ST).
Die Befehle müssen mit einem CR und LF bestätigt werden.
Wird ein Befehl erkannt, wird umgehend der Befehl als Antwort zurück gesendet oder im Fall des Status, der Betriebszustand (AN AU). Ist die Eingabe fehlerhaft, wir ein (ER) zurückgeschickt und die getätigte Eingabe gelöscht.

# Sonstiges
Die Rückmeldung ob der Fernseher ein oder ausgeschaltet ist erfolgt über einen USB-Port des Fernsehers. Dieser wird vom Fernseher beim Ausschalten stromlos geschaltet.
Die IR-LED wird von einem PWM-Port mit 36kHz betrieben.
Um an die Signale zu kommen, die der Fernseher von der Fernbedienung erwartet, wurde die Fernbedinung mit einem IR-Empfänger und einem Logik-Analyser ausgewertet.

Für das zuverlässige Einschalten des Fernsehers wird die Einschaltsequenz mehrmals wiederholt, bis der Fernseher positiv mit einer Spannung am USB-Port antwortet.
Beim Ausschalten besteht die Problematik das die Spannung erst nach einigen Sekunden, nachdem der Fernseher schon ausgeschaltet ist, abfällt.

Es sollte in jedem Fall nach einer Zeit von einigen Sekunden nach dem Ein- oder Ausschalten mit dem Status überprüft werden, ob eine Reaktion erfolgt ist.

Um die Langzeitstabilität weiter zu erhöhen, wurde der Watchdogtimer aktiviert, der einen reboot erzwingt, wenn ein Hängenbleiben festgestellt wurde.

Erweiterungen der Steuerung mit anderen Signalen ist problemlos möglich, beinhaltet jedoch die fehlende Rückmeldung des Fernsehers.
Eine Konfiguration mittels Serialport wäre ebenfalls denkbar, war jedoch nicht notwendig.
