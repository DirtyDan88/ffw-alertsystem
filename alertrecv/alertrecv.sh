#!/bin/bash

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
