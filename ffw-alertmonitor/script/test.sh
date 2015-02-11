#!/bin/bash

case "$1" in
    watchdog)
        echo "Testing watchdog message ..."
        MESSAGE="POCSAG1200: Address:  174896  Function: 0  Alpha:   <BS><CAN>"
        ;;
    alert1)
        echo "Testing alert message ..."
        MESSAGE="POCSAG1200: Address:  158973  Function: 0  Alpha:   11288/bert/Opek/Magen-Darm-Zentrum M/Bismarckplatz 1/MA-Schwetzingerstadt/WB 3 Ida Scipio Heim/Murgstr. 4/MA-Neckarstadt/12, (laut Pflege geht der Bruder zum Bürgermeister sollte es nicht klappen)/"
        ;;
    alert2)
        echo "Testing alert message with geo coordinates ..."
        MESSAGE="POCSAG1200: Address:  158973  Function: 0  Alpha:   49.324291/08.808761/26/F1 undefiniertes Kleinfeuer//vorm Kindergarten/Industriestraße /Meckesheim// //brennende Mülleimer/"
        ;;
    alert3)
	echo "Testing with real pocsag string ..."
	MESSAGE="POCSAG1200: Address:  158973  Function: 0  Alpha:   49.31765/08.80954/329/F2 Kellerbrand///Zeppelinstr. 29 /Meckesheim// //Trocknerbrand/"
	;;
    --help)
        echo "Usage: $0 {watchdog | alert1 | alert2 | <message>}"
        exit 1 
        ;;
    *)
        echo "Testing user defined message ..."
        MESSAGE="POCSAG1200: Address:  158973  Function: 0  Alpha:   $1";
        ;;
esac

echo $MESSAGE | socat - udp-datagram:192.168.1.255:50000,broadcast
