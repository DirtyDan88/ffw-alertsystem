echo "starting chrome..."
locationOfScript=$(dirname "$(readlink -e "$0")")

# start chrome in fullscreen mode with --kiosk
chromium-browser --kiosk file://$locationOfScript/../$1 
