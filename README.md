# ffw-alertsystem

Build requirements:
 * current JDK  (created with jdk 1.8, lesser should work as well)
 * Apache ant 
 * RXTX library 

Linux / Windows:
 * run install.sh / install.bat
 * the PATH variable must contain your java installation, the path to the ant building tool and 
 * also the script/alert.{sh|bat} script uses chrome to present the generated html-files, therefore 
   the path to "chrome.exe" has to be in the PATH variable as well
   
The RXTX library (for COM port communication) brings platform dependent libs:
* Linux: 
    * 'sudo apt-get install librxtx-java' for installation (needs 32-bit JVM?)
    * use run.sh to execute alertmonitor.jar (which sets some path vars for the JVM)
* Windows: not yet tested 
