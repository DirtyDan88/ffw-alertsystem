#!/bin/bash

# emulating the alertrecv-script
echo $1 | socat - udp-datagram:255.255.255.255:50000,broadcast
