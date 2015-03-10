#!/bin/sh

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

### BEGIN INIT INFO
# Provides:          alertrecv-init
# Required-Start:    $all
# Required-Stop:
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: alertrecv-init
# Description:
### END INIT INFO

case "$1" in
  start)
    su pi -c "/home/pi/ffw-alertsystem/alertrecv/alertrecv.sh start &"
    ;;
  stop)
    su pi -c "/home/pi/ffw-alertsystem/alertrecv/alertrecv.sh stop &"
    ;;
  *)
    echo "Usage: $0 {start|stop}"
    exit 1
    ;;
esac

exit 0
