# DataProcessor
#
# Author: Thomas
#
# This script cumulates data from several data files and store
# the aggregated data back to a csv file
#
# usage: main <data-folder> <output-file>

import sys
import os
import json

#
# Read json file
#


def readJsonFile(fileName):
    with open(fileName, 'r') as fileHandle:
        return json.loads(fileHandle.read())

#
# Read json files and put the data into a table format
#


def readData(dataFiles, aggrDataHeader, aggrData):
    for fileName in dataFiles:
        print 'Load data file: ' + fileName
        data = readJsonFile(dataFolder + os.sep + fileName)
        t = data['Name'] + '0'
        props = data['Property']

        for k in props:
            if not k in aggrDataHeader:
                aggrDataHeader.append(k)

        row = [t]
        for k in aggrDataHeader:
            if k in props:
                row.append(props[k])
            else:
                row.append('')

        aggrData.append(row)

#
# Write the given data as csv file
#


def writeDataAsCsv(headers, rows, outputFile):
    with open(outputFile, 'w') as fileHandle:
        line = 'Time;'
        for val in headers:
            line += val + ';'
        fileHandle.write(line + '\n')

        for row in rows:
            line = ''
            for val in row:
                line += str(val) + ';'
            fileHandle.write(line + '\n')


#
#  Main
#

print '------------------'
print '- Data Processor -'
print '------------------'

if len(sys.argv) < 3:
    print 'usage: main <data-folder> <output-file>'
    sys.exit(-1)

dataFolder = sys.argv[1]
outputFile = sys.argv[2]

print 'Data-Folder: ' + dataFolder
print 'Output-File: ' + outputFile
print '------------------'

dataFiles = [f for f in os.listdir(dataFolder) if os.path.isfile(dataFolder + os.sep + f) and f.endswith(".json")]

aggrDataHeader = []
aggrData = []
readData(dataFiles, aggrDataHeader, aggrData)
writeDataAsCsv(aggrDataHeader, aggrData, outputFile)

