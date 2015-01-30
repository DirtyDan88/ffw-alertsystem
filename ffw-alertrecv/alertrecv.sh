echo "start receiving" | socat - udp-datagram:192.168.1.255:50000,broadcast

# send output as broadcast message:
rtl_fm -M nfm -s 22050 -f 173.255.000M -A fast -g 49.60 | multimon-ng -t raw -a POCSAG1200 -f alpha /dev/stdin | socat - udp-datagram:192.168.1.255:50000,broadcast

# send decoded string to a well known client:
#rtl_fm -M nfm -s 22050 -f 173.255.000M -A fast -g 49.60 | multimon-ng -t raw -a POCSAG1200 -f alpha /dev/stdin | nc -u 192.168.1.xxx 50000

# for output to bash:
#rtl_fm -M nfm -s 22050 -f 173.255.000M -A fast -g 49.60 | multimon-ng -t raw -a POCSAG1200 -f alpha /dev/stdin >> /dev/stdout
