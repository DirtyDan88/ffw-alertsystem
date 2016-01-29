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
CLASSPATH=$CLASSPATH":$LOCATION/../3rdparty/twilio-java-sdk-3.7.2-jar-with-dependencies.jar"

JAVASRC="$LOCATION/src/ffw/alertsystem/watchdog/*.java
         $LOCATION/../core/src/ffw/alertsystem/core/*.java
         $LOCATION/../util/src/ffw/alertsystem/util/Logger.java
         $LOCATION/../util/src/ffw/alertsystem/util/DateAndTime.java
         $LOCATION/../util/src/ffw/alertsystem/util/XMLFile.java
         $LOCATION/../util/src/ffw/alertsystem/util/FileReader.java
         $LOCATION/../util/src/ffw/alertsystem/util/ConfigReader.java
         $LOCATION/../util/src/ffw/alertsystem/util/SQLiteConnection.java
         $LOCATION/../util/src/ffw/alertsystem/util/Mail.java
         $LOCATION/../util/src/ffw/alertsystem/util/TwilioSMS.java"

BUILDFILE="$LOCATION/build-watchdog.xml"



LOCKFILE=$LOCATION"/../../.watchdog.lock"
if [ -e $LOCKFILE ]; then
  WAS_RUNNING=TRUE
  sh $LOCATION/../../run-watchdog.sh stop
  echo ">> stopped watchdog"
fi

sh $LOCATION/../core/build.sh "ffw-watchdog.jar" \
   $LOCATION $CLASSPATH "$JAVASRC" $BUILDFILE

if [ "$WAS_RUNNING" = TRUE ]; then
  sh $LOCATION/../../run-watchdog.sh start start
  echo ">> re-started watchdog"
fi
