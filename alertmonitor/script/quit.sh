# kill: - Chromium
#       - PDF-Viewer
#       - File-Manager
#       - Image-Viewer

# and in addition: send signal to turn off TV


# Chromium
while true; do
    killall chromium
    if [ -z "$(ps -e | grep chromium)" ]; then
        break;
    fi
done

