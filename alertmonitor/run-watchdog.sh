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
                    nohup java -jar ffw-alertsystem-watchdog.jar -logInFile >> /dev/null &
                    # write process id to lock-file
                    PROCESSID="PROCESS-ID: "$!
                    echo $PROCESSID > ".watchdog.lock"
                fi
                ;;
            stop)
                # get the process id from the lock-file
                for PID in $(cat .watchdog.lock | grep PROCESS-ID) ; do
                    PROCESSID=$PID
                done
                echo "kill watchdog process: "$PROCESSID
                kill $PROCESSID
                rm .watchdog.lock
                
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
