#!/bin/bash

# start/stop the watchdog
LOCATION=$(dirname "$(readlink -e "$0")")
LOCKFILE=$LOCATION"/.watchdog.lock"
JARFILE=$LOCATION"/ffw-alertsystem-watchdog.jar"

case "$1" in
  start)
    if [ -e $LOCKFILE ]; then
      echo "watchdog is already running"
    else
      # run app in background; output to log file
      nohup java -jar $JARFILE -logInFile >> /dev/null &
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
    
  *)
    echo "Usage: $0 {start|stop}"
    exit 1
    ;;
esac
