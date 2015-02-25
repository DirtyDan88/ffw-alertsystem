#!/bin/bash

ALERTMONITOR_PORT=50000

# Configure Java for RXTX on embedded platforms like Raspberry Pi (Raspbian)
if [ -e "/dev/ttyAMA0" ] || [ -e "/dev/ttyO0" ]
then
    for port in `find /dev -name 'tty*'`
    do
        PORTS="$PORTS:$port"
    done
    JAVA_OPT="-Djava.library.path=/usr/lib/jni -Dgnu.io.rxtx.SerialPorts=$PORTS"
fi

# chromium needs this
export DISPLAY=":0" 

# start the alertmonitor
case "$1" in
    -bg)
        case "$2" in
            start)
                if [ -n "$(netstat -a | grep $ALERTMONITOR_PORT)" ] ; then
                    echo "alertmonitor is already running"
                else
                    # run app in background; input = null and output to log file
                    DATE=`date +%d-%m-%Y`
                    LOGFILE="log/log-"$DATE"-alertmonitor.txt"
                    nohup java $JAVA_OPT -jar ffw-alertmonitor.jar < /dev/null >> $LOGFILE &
                    # write process id to logfile
                    PROCESSID="PROCESS-ID: "$!
                    echo $PROCESSID >> $LOGFILE
                fi
                ;;
            stop)
                # loop over all process id's and select the last one
                for ID in $(cat log/*-alertmonitor.txt | grep PROCESS-ID) ; do
                    PROCESSID=$ID
                done
                echo "kill alertmonitor process: "$PROCESSID
                kill $PROCESSID
                ;;
            *)
                echo "Usage: $0 {-bg start | -bg stop}"
                exit 1
                ;;
        esac
        ;;
    *)
        java $JAVA_OPT -jar ffw-alertmonitor.jar
        ;;
esac
