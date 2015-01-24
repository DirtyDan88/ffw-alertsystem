if [ ! -d bin/ ]; then
    echo ">> make dir bin/"
    mkdir bin/
fi

if [ ! -d log/ ]; then
    echo ">> make dir log/"
    mkdir log/
fi

echo ">> compiling source files ..."
javac -g:none \
      -d bin/ \
      -cp lib/* \
      src/ffw/alertsystem/listener/*.java \
      src/ffw/alertsystem/message/*.java \
      src/ffw/alertsystem/*.java

echo ">> let ant do the building work ..."
ant 
