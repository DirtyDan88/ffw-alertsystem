#!/bin/bash

#  Copyright (c) 2015-2016, Max Stark <max.stark88@web.de>
#    All rights reserved.
#
#  This file is part of ffw-alertsystem, which is free software: you
#  can redistribute it and/or modify it under the terms of the GNU
#  General Public License as published by the Free Software Foundation,
#  either version 2 of the License, or (at your option) any later
#  version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
#  General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, see <http://www.gnu.org/licenses/>.



# start/stop the watchdog
LOCATION=$(dirname "$(readlink -e "$0")")
LOCKFILE=$LOCATION"/.watchdog.lock"
JARFILE=$LOCATION"/build/ffw-alertsystem-watchdog.jar"

case "$1" in
  start)
    if [ -e $LOCKFILE ]; then
      echo "watchdog is already running"
    else
      # run app in background; output to log file
      nohup java -jar $JARFILE -config config-watchdog.xml -logInFile -logLevel 4 >> /dev/null &
      # write process id to lock-file
      PROCESSID="PROCESS-ID: "$!
      echo $PROCESSID > $LOCKFILE
    fi
    ;;
    
  stop)
    # get the process id from the lock-file
    if [ -e $LOCKFILE ]; then
      for PID in $(cat $LOCKFILE | grep PROCESS-ID); do
        PROCESSID=$PID
      done
      echo "kill watchdog process: "$PROCESSID
      kill -15 $PROCESSID
      rm $LOCKFILE
    else
      echo "watchdog is not running"    
    fi
    ;;

  debug)
    java $JAVA_OPT -jar $JARFILE -config config-watchdog.xml -logLevel 5
    ;;
    
  *)
    echo "Usage: $0 {start|stop|debug}"
    exit 1
    ;;
esac
