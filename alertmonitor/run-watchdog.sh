#!/bin/bash

WATCHDOG_PORT=50001

# start the watchdog
case "$1" in
    -bg)
        case "$2" in
            start)
                if [ -n "$(netstat -a | grep $WATCHDOG_PORT)" ] ; then
                    echo "watchdog is already running"
                else
                    # run app in background; output to log file
                    DATE=`date +%d-%m-%Y`
                    LOGFILE="log/log-"$DATE"-watchdog.txt"
                    nohup java -jar ffw-alertsystem-watchdog.jar >> $LOGFILE &
                    # write process id to logfile
                    PROCESSID="PROCESS-ID: "$!
                    echo $PROCESSID >> $LOGFILE
                fi
                ;;
            stop)
                # loop over all process id's and select the last one
                for ID in $(cat log/*-watchdog.txt | grep PROCESS-ID) ; do
                    PROCESSID=$ID
                done
                echo "kill watchdog process: "$PROCESSID
                kill $PROCESSID
                ;;
            *)
                echo "Usage: $0 {-bg start | -bg stop}"
                exit 1
                ;;
        esac
        ;;
    *)
        java $JAVA_OPT -jar ffw-alertsystem-watchdog.jar
        ;;
esac
