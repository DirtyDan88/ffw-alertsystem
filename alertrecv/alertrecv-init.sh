#!/bin/sh

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