#!/bin/bash

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
JARFILE=$LOCATION"/ffw-alertmonitor.jar"

case "$1" in
  start)
    if [ -e $LOCKFILE ]; then
      echo "alertmonitor is already running"
    else
      # run app in background; input and output = null and log into file
	  nohup java $JAVA_OPT -jar $JARFILE -logInFile < /dev/null >> /dev/null &
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
    java $JAVA_OPT -jar $JARFILE
    ;;
    
  *)
    echo "Usage: $0 {start|stop|debug}"
    exit 1
    ;;
esac
