#!/bin/bash

WATCHDOG_PORT=50001

# start the watchdog
case "$1" in
    -bg)
        LOCKFILE=$(dirname "$(readlink -e "$0")")"/.watchdog.lock"

        case "$2" in
            start)
                if [ -e $LOCKFILE ] ; then
                    echo "watchdog is already running"
                else
                    # run app in background; output to log file
                    nohup java -jar ffw-alertsystem-watchdog.jar -logInFile >> /dev/null &
                    # write process id to lock-file
                    PROCESSID="PROCESS-ID: "$!
                    echo $PROCESSID > $LOCKFILE
                fi
                ;;
            stop)
                # get the process id from the lock-file
                for PID in $(cat $LOCKFILE | grep PROCESS-ID) ; do
                    PROCESSID=$PID
                done
                echo "kill watchdog process: "$PROCESSID
                kill $PROCESSID
                rm $LOCKFILE
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
