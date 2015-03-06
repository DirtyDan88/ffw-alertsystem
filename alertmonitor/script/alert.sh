# first, force chromium to quit
while true; do
    killall chromium
    if [ -z "$(ps -e | grep chromium)" ]; then
        break;
    fi
done

# start chrome in fullscreen mode with --kiosk
# disable chrome session restore functionality with --incognito
locationOfScript=$(dirname "$(readlink -e "$0")")
chromium-browser --kiosk --incognito file://$locationOfScript/../$1 
