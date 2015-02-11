#
# Configure Java for RXTX on embedded platforms like Raspberry Pi (Raspbian)
#
if [ -e "/dev/ttyAMA0" ] || [ -e "/dev/ttyO0" ]
then
  for port in `find /dev -name 'tty*'`
  do
    PORTS="$PORTS:$port"
  done
  JAVA_OPT="-Djava.library.path=/usr/lib/jni -Dgnu.io.rxtx.SerialPorts=$PORTS"
fi

export DISPLAY=":0" # chromium needs this

case "$1" in
    bg)
    echo "execution in background"
    # run application in background, input = null and output to alertmonitor.log
    nohup java $JAVA_OPT -jar ffw-alertmonitor.jar < /dev/null >> log/alertmonitor.log &
    ;;
    
    *)
    java $JAVA_OPT -jar ffw-alertmonitor.jar
    ;;
esac
