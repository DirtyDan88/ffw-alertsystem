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



echo "#########################################################################"
echo ">> building "$1

# option aborts the script execution if one of the commands failed
set -e

# get the params
LOCATION=$2
CLASSPATH=$3
JAVASRC=$4
BUILDFILE=$5

# create required folders
if [ ! -d $LOCATION/bin ]; then
  mkdir $LOCATION/bin
  echo ">> created bin-folder"
fi

# compilation process
echo ">> compiling source files ..."
echo "    >> classpath:      $CLASSPATH"
echo "    >> source-file(s): $JAVASRC"
javac -g:none -d $LOCATION/bin/ -cp $CLASSPATH $JAVASRC
echo ">> compilation done"

# build process
echo ">> let ant do the building work ..."
ant -buildfile $BUILDFILE

# remove compilation files
rm $LOCATION/bin -r
echo ">> removed intermediate files"

echo ">> finished building "$1
echo "#########################################################################"
