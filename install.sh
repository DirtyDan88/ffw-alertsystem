if [ ! -d bin/ ]; then
    echo ">> make dir bin/"
    mkdir bin/
fi

echo ">> compiling source files"
javac -g:none \
      -d bin/ \
      -cp lib/* \
      src/ffw/alertsystem/listener/*.java \
      src/ffw/alertsystem/message/*.java \
      src/ffw/alertsystem/*.java



echo ">> building ffw-alertsystem.jar"
echo ">> building ffw-alertsystem-watchdog.jar"
ant 

#cd lib/
#mkdir tmp/
#cd tmp/
#jar xf ../jsoup-1.7.3.jar
#cd ..
#cd ..
#jar cvfm ffw-alertsystem.jar alertsystem.mf lib/tmp -C bin/ .
#cd lib
#rm tmp -r
#cd ..


#jar cvfm ffw-alertsystem-watchdog.jar watchdog.mf -C bin/ .
