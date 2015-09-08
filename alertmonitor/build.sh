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
      src/ffw/*.java \
      src/ffw/util/*.java \
      src/ffw/util/logging/*.java \
      src/ffw/alertlistener/*.java \
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
