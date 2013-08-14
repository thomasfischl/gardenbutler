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
def readData(dataFolder, dataFiles, aggrDataHeader, aggrData):
    for fileName in dataFiles:
        print 'Load data file: ' + fileName
        data = readJsonFile(dataFolder + os.sep + fileName)
        for k, v in data.iteritems():
            addDataRow(aggrDataHeader, aggrData, k, v)
    aggrDataHeader.insert(0, 'Time')


#
# Analyze a json object and transform that into a row format
#
def addDataRow(aggrDataHeader, aggrData, name, props):
        for k in props:
            if not k in aggrDataHeader:
                aggrDataHeader.append(k)

        row = [name]
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
        line = ''
        for val in headers:
            line += val + ';'
        fileHandle.write(line + '\n')

        for row in rows:
            line = ''
            for val in row:
                line += str(val) + ';'
            fileHandle.write(line + '\n')


def avg(val1, val2):
    return round((float(val1) + float(val2))/2, 2)


def aggregateRows(dataHeader, dataRows, dataAggrRows):

    tmpRow = []
    currTime = ''
    merge = False

    for row in dataRows:
        for idx in range(len(row)):
            if dataHeader[idx] == 'Time':
                tmpTime = row[idx][:-4] + '0-00'
                if currTime != tmpTime:
                    dataAggrRows.append(tmpRow)
                    currTime = tmpTime
                    tmpRow = [tmpTime]
                    merge = False
                else:
                    merge = True
            elif dataHeader[idx] == 'Pump':
                if merge:
                    tmpRow[idx] = max(tmpRow[idx], row[idx])
                else:
                    tmpRow.append(row[idx])
            else:
                if merge:
                    tmpRow[idx] = avg(tmpRow[idx], row[idx])
                else:
                    tmpRow.append(row[idx])

if __name__ == "__main__":
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

    dataHeader = []
    dataRows = []

    readData(dataFolder, dataFiles, dataHeader, dataRows)
    dataRows.sort(key=lambda x: x[0])
    #writeDataAsCsv(dataHeader, dataRows, outputFile)

    dataAggrRows = []
    aggregateRows(dataHeader, dataRows, dataAggrRows)
    writeDataAsCsv(dataHeader, dataAggrRows, outputFile)