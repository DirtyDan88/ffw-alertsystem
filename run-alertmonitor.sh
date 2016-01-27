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



# Configure Java for RXTX on embedded platforms like Raspberry Pi (Raspbian)
if [ -e "/dev/ttyAMA0" ] || [ -e "/dev/ttyO0" ]; then
  for port in `find /dev -name 'tty*'`; do
    PORTS="$PORTS:$port"
  done
  JAVA_OPT="-Djava.library.path=/usr/lib/jni -Dgnu.io.rxtx.SerialPorts=$PORTS"
fi

# chromium needs this
export DISPLAY=":0" 



# start/stop the alertmonitor
LOCATION=$(dirname "$(readlink -e "$0")")
LOCKFILE=$LOCATION"/.alertmonitor.lock"
JARFILE=$LOCATION"/build/ffw-alertsystem-monitor.jar"

case "$1" in
  start)
    if [ -e $LOCKFILE ]; then
      echo "alertmonitor is already running"
    else
      # run app in background; input and output = null and log into file
	    nohup java $JAVA_OPT -jar $JARFILE -config config-monitor.xml -logInFile -logLevel 4 < /dev/null >> /dev/null &
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
      echo "kill alertmonitor process: "$PROCESSID
	  kill -15 $PROCESSID
      rm $LOCKFILE
    else
      echo "alertmonitor is not running"
    fi
    ;;
    
  debug)
    java $JAVA_OPT -jar $JARFILE -config config-monitor.xml -logLevel 5
    ;;
    
  *)
    echo "Usage: $0 {start|stop|debug}"
    exit 1
    ;;
esac
