#!/bin/sh

### BEGIN INIT INFO
# Provides:          alertmonitor-init
# Required-Start:    $all
# Required-Stop:
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: alertrecv-monitor
# Description:
### END INIT INFO

case "$1" in
    start)
#        su pi -c "rm /home/pi/ffw-alertsystem/alertmonitor/.alertmonitor.lock"
        su pi -c "/home/pi/ffw-alertsystem/alertmonitor/run-alertmonitor.sh -bg start"
        ;;
    stop)
        su pi -c "/home/pi/ffw-alertsystem/alertmonitor/run-alertmonitor.sh -bg stop"
        ;;
    *)
        echo "Usage: $0 {start|stop}"
        exit 1
        ;;
esac

exit 0
