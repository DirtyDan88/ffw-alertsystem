#!/usr/bin/python

################################################################################
#                                                                              #
#  Copyright (c) 2015, Max Stark <max.stark88@web.de>                          #
#    All rights reserved.                                                      #
#                                                                              #
#  This file is part of ffw-alertsystem, which is free software: you           #
#  can redistribute it and/or modify it under the terms of the GNU             #
#  General Public License as published by the Free Software Foundation,        #
#  either version 2 of the License, or (at your option) any later              #
#  version.                                                                    #
#                                                                              #
#  This program is distributed in the hope that it will be useful,             #
#  but WITHOUT ANY WARRANTY; without even the implied warranty of              #
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU            #
#  General Public License for more details.                                    #
#                                                                              #
#  You should have received a copy of the GNU General Public License           #
#  along with this program; if not, see <http://www.gnu.org/licenses/>.        #
#                                                                              #
################################################################################

import sys, subprocess, os, time

################################################################################
def evalLogFile(logFile):
  if logFile.endswith('2015.txt'):
    global nFiles    
    nFiles += 1
    print 'Open file:   \'' + logFile + '\''

    for line in open(logFile):
      timestamp = getTimestamp(line)
      message   = getMessage(line)
      
      #echo $MESSAGE | socat - udp-datagram:255.255.255.255:50000,broadcast
      print 'send message: \'' + message + '\''
      proc = subprocess.Popen(['sh', 'send-message.sh', message], 
                              stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
      time.sleep(0.05)
      #sys.exit(2)

    print 'Close file:  \'' + logFile + '\''

################################################################################
def getTimestamp(line):
  dateStartPos = line.find('[') + 1
  dateEndPos   = line.find(']')
  date = line[dateStartPos:dateEndPos]

  # format in logfile: '14-04-2015 # 14:24:39'
  struct = time.strptime(date, "%d-%m-%Y # %H:%M:%S")
  timestamp = time.mktime(struct)
  
  return timestamp

################################################################################
def getMessage(line):
  messageStartPos = line.find('POCSAG1200')
  return line[messageStartPos:len(line) - 1]

################################################################################
# the main script:

rootFolder = sys.argv[1]
nFiles = 0


# !!! Run only with no logging in alertmonitor !!!
# Otherwise endless loop is possible, because alertmonitor creates new logfiles
# which were read from this script again

# command line option: noLogging
#proc = subprocess.Popen(['sh', '../run-alertmonitor.sh.py', 'start'], 
#                        stdout=subprocess.PIPE, stderr=subprocess.STDOUT)



print 'Evaluating logfiles in folder \'' + rootFolder + '\''
for curFile in os.listdir(rootFolder):
  if os.path.isdir(rootFolder + '/' + curFile):
    print 'Enter folder \'' + rootFolder + curFile + '\''
    for logFile in os.listdir(rootFolder + '/' + curFile):
      evalLogFile(rootFolder + curFile + '/' + logFile)
  else:
    evalLogFile(rootFolder + curFile)

print 'Total files read: ' + str(nFiles)


#proc = subprocess.Popen(['sh', '../run-alertmonitor.sh.py', 'stop'], 
#                        stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

