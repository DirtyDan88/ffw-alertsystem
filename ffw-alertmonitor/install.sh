if [ ! -d bin ]; then
    echo ">> make dir bin"
    mkdir bin/
fi
cd html
if [ ! -d alerts ]; then
    echo ">> make dir html/alerts"
    mkdir alerts
fi
cd ..
if [ ! -d log ]; then
    echo ">> make dir log"
    mkdir log
fi

echo ">> compiling source files ..."
javac -g:none \
      -d bin/ \
      -cp lib/* \
      src/ffw/alertmonitor/util/*.java \
      src/ffw/alertmonitor/*.java

echo ">> let ant do the building work ..."
ant 
