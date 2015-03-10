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

# pocsag message port
PORT=50000

# start receiver and decoder; send POCSAG1200 string via broadcast to local network
start_receiving() {
  initstr="started receiving data at "$(date)
  echo $initstr | socat - udp-datagram:255.255.255.255:$PORT,broadcast

  nohup rtl_fm -M nfm -s 22050 -f 173.255.000M -A fast -g 49.60 | multimon-ng -t raw -a POCSAG1200 -f alpha /dev/stdin | socat - udp-datagram:255.255.255.255:$PORT,broadcast &
}

# kills the (main-)receiver process and notifies all observers in local ip-network
stop_receiving() {
  pkill rtl_fm

  termstr="terminated receiving data at "$(date)
  echo $termstr | socat - udp-datagram:255.255.255.255:$PORT,broadcast
}

case "$1" in
  start)
    echo "Starting raspi-alertrecv ..."
    start_receiving
    ;;
  stop)
    echo "Stopping raspi-alertrecv ..."
    stop_receiving
    ;;
  *)
    echo "Usage: $0 {start|stop}"
    exit 1
    ;;
esac

exit 0
