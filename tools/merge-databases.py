#!/usr/bin/python

################################################################################
#                                                                              #
#  Copyright (c) 2015-2016, Max Stark <max.stark88@web.de>                     #
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

import sys, sqlite3, os

################################################################################
# create merge database
mergeDBName = sys.argv[1] #'2015-08-Aug.db'
mergeDB = sqlite3.connect(mergeDBName)
mergeDBCursor = mergeDB.cursor()

fd = open('sql-commands/sql-createAlertMessageTable.sql', 'r')
sqlCreateMergeDB = fd.read()
fd.close()

mergeDBCursor.execute(sqlCreateMergeDB)

# loop over all databases in selected merge-folder
fd = open('sql-commands/sql-selectAllForMerge.sql', 'r')
sqlSelectAllForMerge = fd.read()
fd.close()

fd = open('sql-commands/sql-insertAlertMessage.sql', 'r')
sqlInsertAlertMessage = fd.read()
fd.close()

folderToMerge = sys.argv[2] #'../2015-08-Aug/'

for dbFile in os.listdir(folderToMerge):
  if dbFile.endswith('.db'):
    #dbName = '04-08-2015.db'
    curDB = sqlite3.connect(folderToMerge + dbFile)
    curDBCursor = curDB.cursor()
    curDBCursor.execute(sqlSelectAllForMerge)

    for alertMessage in curDBCursor.fetchall():
      mergeDBCursor.execute(sqlInsertAlertMessage, alertMessage)

    mergeDB.commit();
    curDBCursor.close()
    curDB.close()

mergeDBCursor.close()
mergeDB.close();
