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



# option aborts the script execution if one of the commands failed
set -e

LOCATION=$(dirname "$(readlink -e "$0")")

echo "build alert-actions"

sh $LOCATION/build-alertactions/build-AlertMailInformer-action.sh
echo "\n"

sh $LOCATION/build-alertactions/build-AlertSMSInformer-action.sh
echo "\n"

sh $LOCATION/build-alertactions/build-AlertSpeaker-action.sh
echo "\n"

sh $LOCATION/build-alertactions/build-HtmlBuilder-action.sh
echo "\n"

sh $LOCATION/build-alertactions/build-SystemHibernate-action.sh
echo "\n"

sh $LOCATION/build-alertactions/build-TVSwitchOn-action.sh
echo "\n"
