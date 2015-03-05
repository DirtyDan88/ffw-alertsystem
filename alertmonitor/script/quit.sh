# kill: - Chromium
#       - PDF-Viewer
#       - File-Manager
#       - Image-Viewer

# and in addition: send signal to turn off TV


# Chromium
while true; do
    killall chromium-browser
    if [ -z "$(ps -e | grep chromium-browse)" ]; then
        break;
    fi
done

