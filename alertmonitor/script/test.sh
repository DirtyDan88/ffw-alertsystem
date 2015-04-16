#!/bin/bash

#    Copyright (c) 2015, Max Stark <max.stark88@web.de> 
#        All rights reserved.
#    
#    This file is part of ffw-alertsystem, which is free software: you 
#    can redistribute it and/or modify it under the terms of the GNU 
#    General Public License as published by the Free Software Foundation, 
#    either version 2 of the License, or (at your option) any later 
#    version.
#    
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
#    General Public License for more details. 
#    
#    You should have received a copy of the GNU General Public License 
#    along with this program; if not, see <http://www.gnu.org/licenses/>.

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
  alert4)
    echo "Testing with real pocsag string ..."
    MESSAGE="POCSAG1200: Address:  158973  Function: 0  Alpha:   49.33422/08.85146/518/F1 Feuer klein//FW Meckesheim-Mönchz/Mühlstr. 12/Meckesheim-Mönchzell// //Test Test/<NUL><NUL>"
    ;;
  alert5)
    echo "Alert string from 14/04/2015"
    MESSAGE="POCSAG1200: Address:  158973  Function: 0  Alpha:   //1202/F2 LKW/Bus//Ortsausgang/Eschelbronner Str. /Meckesheim// //Maisernter/"
    ;;
    
  --help)
    echo "Usage: $0 {watchdog|alert[1-4]|<message>}"
    exit 1 
    ;;
  *)
    echo "Testing user defined message ..."
    MESSAGE="POCSAG1200: Address:  158973  Function: 0  Alpha:   $1";
    ;;
esac

echo $MESSAGE | socat - udp-datagram:255.255.255.255:50000,broadcast
