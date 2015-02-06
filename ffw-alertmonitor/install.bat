@Echo off
IF not exist bin (
    echo ">> make dir bin"
    mkdir bin
)
cd html
IF not exist alerts (
    echo ">> make dir html/alerts"
    mkdir alerts
)
cd ..
IF not exist log (
    echo ">> make dir log"
    mkdir log
)

echo ">> compiling source files"
javac -g:none -d bin/ -cp "lib/*" src/ffw/util/*.java src/ffw/alertmonitor/*.java

echo ">> let ant do the building work ..."
ant 
