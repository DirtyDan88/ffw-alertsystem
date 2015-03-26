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

# first, force chromium to quit
# TODO: call close-application-script
while true; do
  killall chromium
  killall chromium-browse
  if [ -z "$(ps -e | grep chromium)" ]; then
    break;
  fi
done

# start chrome in fullscreen mode with --kiosk
# disable chrome session restore functionality with --incognito
locationOfScript=$(dirname "$(readlink -e "$0")")
chromium-browser --kiosk --incognito file://$locationOfScript/../$1 
