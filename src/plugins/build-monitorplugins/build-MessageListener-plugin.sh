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

CLASSPATH="build/ffw-alertsystem-monitor.jar"
CLASSPATH=$CLASSPATH":$LOCATION/../../3rdparty/jetty/*"

JAVASRC="$LOCATION/../src/ffw/alertsystem/plugins/monitor/MessageListener.java
         $LOCATION/../../core/src/ffw/alertsystem/core/receiver/ReceiverServer.java
         $LOCATION/../../core/src/ffw/alertsystem/core/receiver/ReceiverWebSocket.java
         $LOCATION/../../util/src/ffw/alertsystem/util/JettyLogger.java
         $LOCATION/../../util/src/ffw/alertsystem/util/JettyWebSocket.java"

BUILDFILE="$LOCATION/build-MessageListener-plugin.xml"

sh $LOCATION/../../core/build.sh "MessageListener-plugin" \
   $LOCATION $CLASSPATH "$JAVASRC" $BUILDFILE
