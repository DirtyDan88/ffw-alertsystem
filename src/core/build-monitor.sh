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



LOCATION=$(dirname "$(readlink -e "$0")")

CLASSPATH="$LOCATION/../3rdparty/jcommander-1.48.jar"
CLASSPATH=$CLASSPATH":$LOCATION/../3rdparty/javax.mail.jar"

JAVASRC="$LOCATION/src/ffw/alertsystem/core/*.java
         $LOCATION/src/ffw/alertsystem/core/alertaction/*.java
         $LOCATION/src/ffw/alertsystem/core/message/*.java
         $LOCATION/src/ffw/alertsystem/core/monitor/*.java
         $LOCATION/src/ffw/alertsystem/core/plugin/*.java
         $LOCATION/../util/src/ffw/alertsystem/util/Logger.java
         $LOCATION/../util/src/ffw/alertsystem/util/Mail.java
         $LOCATION/../util/src/ffw/alertsystem/util/DateAndTime.java
         $LOCATION/../util/src/ffw/alertsystem/util/XMLFile.java"

BUILDFILE="$LOCATION/build-monitor.xml"



LOCKFILE=$LOCATION"/../../.alertmonitor.lock"
if [ -e $LOCKFILE ]; then
  WAS_RUNNING=TRUE
  sh $LOCATION/../../run-alertmonitor.sh stop
  echo ">> stopped alertmonitor"
fi

sh $LOCATION/build.sh "ffw-alertsystem-monitor.jar" \
   $LOCATION $CLASSPATH "$JAVASRC" $BUILDFILE

if [ "$WAS_RUNNING" = TRUE ]; then
  sh $LOCATION/../../run-alertmonitor.sh start
  echo ">> re-started alertmonitor"
fi
