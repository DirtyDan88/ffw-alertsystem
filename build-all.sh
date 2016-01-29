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

# create folders
if [ ! -d data ]; then
  echo ">> make dir data"
  mkdir data/
fi

echo ">> build core system"
sh src/core/build-monitor.sh
sh src/core/build-receiver.sh

echo ">> build plugins"
sh src/plugins/build-plugins.sh
sh src/plugins/build-actions.sh

echo ">> build watchdog"
sh src/watchdog/build-watchdog.sh
