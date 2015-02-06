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
java $JAVA_OPT -jar $1
