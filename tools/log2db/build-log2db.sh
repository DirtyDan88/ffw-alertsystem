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

CLASSPATH="$LOCATION/../../src/3rdparty/jcommander-1.48.jar"
CLASSPATH=$CLASSPATH":$LOCATION/../../src/3rdparty/javax.mail.jar"

JAVASRC="$LOCATION/src/ffw/alertsystem/tools/log2db/Log2DB.java
         $LOCATION/../../src/core/src/ffw/alertsystem/core/*.java
         $LOCATION/../../src/core/src/ffw/alertsystem/core/message/*.java
         $LOCATION/../../src/util/src/ffw/alertsystem/util/Logger.java
         $LOCATION/../../src/util/src/ffw/alertsystem/util/DateAndTime.java
         $LOCATION/../../src/util/src/ffw/alertsystem/util/XMLFile.java
         $LOCATION/../../src/util/src/ffw/alertsystem/util/Mail.java
         $LOCATION/../../src/util/src/ffw/alertsystem/util/SQLiteConnection.java"

BUILDFILE="$LOCATION/build-log2db.xml"

sh $LOCATION/../../src/core/build.sh "ffw-alertsystem-log2db.jar" \
   $LOCATION $CLASSPATH "$JAVASRC" $BUILDFILE
