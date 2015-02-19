#!/bin/bash

# pocsag message port
PORT=50000

### determines the broadcast addess of the current network ###
get_broadcast_address() {
  # get all IPs (command depends on local language)
  lang=$(locale | grep LANG | cut -d= -f2 | cut -d_ -f1)
  if [ $lang=="de" ]
  then
    all_ips=$(ifconfig | awk '/inet Adresse/{print substr($2,9)}')
  elif [ $lan=="en" ]
  then
    all_ips=$(ifconfig | awk '/inet addr/{print substr($2,6)}')
  fi

  # loop over all IPs
  for ip in $all_ips ; do
    if [ $ip != "127.0.0.1" ]
    then
      # set the seperator to '.'
      IFS=$'.'
      i=0
      for n in $ip ; do
        i=$((i+1))
        if [ $i -eq 1 ]
        then
          bcast=$n
        elif [ $i -eq 4 ]
        then
          bcast=$bcast".255"
        else
          bcast=$bcast"."$n
        fi
      done

       # return
      echo "$bcast"
    fi
    # set the seperator back to space
    IFS=$' '
  done
}

### start receiver and decoder; send POCSAG1200 string via broadcast to determined ip-network ###
start_receiving() {
  bcast=$(get_broadcast_address)

  initstr="started receiving data at "$(date)
  echo $initstr | socat - udp-datagram:$bcast:$PORT,broadcast
  nohup rtl_fm -M nfm -s 22050 -f 173.255.000M -A fast -g 49.60 | multimon-ng -t raw -a POCSAG1200 -f alpha /dev/stdin | socat - udp-datagram:$bcast:$PORT,broadcast &
}

### kills the (main-)receiver process and notifies all observers in local ip-network ###
stop_receiving() {
  pkill rtl_fm
  bcast=$(get_broadcast_address)

  termstr="terminated receiving data at "$(date)
  echo $termstr | socat - udp-datagram:$bcast:$PORT,broadcast
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

