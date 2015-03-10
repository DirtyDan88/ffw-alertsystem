#!/bin/bash

# if the applications are runnning, stop them
LOCATION=$(dirname "$(readlink -e "$0")")
AE_LOCKFILE=$LOCATION"/.alertmonitor.lock"
WD_LOCKFILE=$LOCATION"/.watchdog.lock"

if [ -e $AE_LOCKFILE ]; then
    AM_WAS_RUNNING=TRUE
    sh run-alertmonitor.sh stop
    echo ">> stopped alertmonitor"
fi
if [ -e $WD_LOCKFILE ]; then 
    WD_WAS_RUNNING=TRUE
    sh run-watchdog.sh stop
    echo ">> stopped watchdog"
fi

# build process
echo ">> start build process"
if [ ! -d bin ]; then
    echo ">> make dir bin"
    mkdir bin/
fi
cd html
if [ ! -d alerts ]; then
    echo ">> make dir html/alerts"
    mkdir alerts
fi
cd ..
if [ ! -d log ]; then
    echo ">> make dir log"
    mkdir log
fi

echo ">> compiling source files ..."
javac -g:none \
      -d bin/ \
      -cp "lib/*" \
      src/ffw/util/*.java \
      src/ffw/alertmonitor/*.java \
      src/ffw/alertmonitor/actions/*.java \
      src/ffw/watchdog/*.java

echo ">> let ant do the building work ..."
ant

# start the applications, if they were running
if [ "$AM_WAS_RUNNING" = TRUE ]; then
    echo ">> start alertmonitor"
    sh run-alertmonitor.sh start
fi
if [ "$WD_WAS_RUNNING" = TRUE ]; then 
    echo ">> start watchdog"
    sh run-watchdog.sh start
fi
