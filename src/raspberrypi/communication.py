# Arduino Data Logger
#
# Author: Thomas
#
# This scripts logs all data from the serial arduino interface to an file.
# Each data sequence is stored in one file
#
# usage: Watcher <output-folder> <device>

import os
import json
import time
import datetime
import serial
import urllib2
import communication_config as config
import logging


#
# functions
#
def getCurrentTime():
    return datetime.datetime.now()


def getCorrectedTime():
    return getCurrentTime() + datetime.timedelta(seconds=timeDiff)


def getTimeDiff():
    response = urllib2.urlopen('http://wwp.greenwichmeantime.com/time/scripts/clock-8/x.php')
    content = response.read()
    newTime = int(float(content)) / 1000
    curTime = time.mktime(getCurrentTime().timetuple())
    diffTime = (newTime - int(curTime)) / 1000
    #print "Inet Timestamp: " + str(newTime)
    #print "Curr Timestamp: " + str(curTime)
    #print "Diff (seconds): " + str(diffTime)

    return diffTime


def defineTimeDiff():
    global timeDiff
    timeDiff = getTimeDiff()


def createFolder(name):
    if not os.path.exists(name):
        os.makedirs(name)


def processSerialData(line):
    result = {}
    line = line.strip(' \n\r')
    if line.startswith('[start]') and line.endswith('[end]'):
        for value in line[7:-5].split(';'):
            if '=' in value:
                data = value.split('=')
                result[data[0].strip(' \n\r')] = data[1].strip(' \n\r')
    return result


#
#  class: Communicator
#  This class communicates is a mock
#  implementation for the SerialCommunicator
#
class Communicator:
    device = ""

    def __init__(self, device):
        self.device = device

    def read(self):
        line = '[start]T1=1;T2=2;Pump=0[end]'
        result = processSerialData(line)

        if len(result) == 0:
            logging.info('Unknown data received: ' + line)

        return result

    def get_device(self):
        return self.device

    def write(self, command):
        print 'Write: ' + command


#
#  class: SerialCommunicator
#  This class SerialCommunicator with the
#  arduino board over a serial wire
#
class SerialCommunicator:
    device = ""

    def __init__(self, device):
        self.device = device
        self.ser = serial.Serial(device, 9600)

    def read(self):
        line = self.ser.readline()
        result = processSerialData(line)

        if len(result) == 0:
            logging.info('Unknown data received: ' + line)

        return result

    def get_device(self):
        return self.device

    def write(self, command):
        print 'Write: ' + command
        self.ser.write(command)


#
#  class: DataStore
#  The DataStore aggregates data and write
#  this data in json format to a file
#
class DataStore:
    data = {}

    def __init__(self, folder):
        self.folder = folder
        self.datafolder = folder + os.sep + 'data'
        self.schedulefolder = folder + os.sep + 'schedule'
        self.actionfolder = folder + os.sep + 'actions'
        self.currtime = self.getCurrTime()
        createFolder(self.folder)
        createFolder(self.datafolder)
        createFolder(self.schedulefolder)
        createFolder(self.actionfolder)

    def addData(self, data):
        print 'add data ...'
        time = self.getCurrTime()
        if self.currtime.strftime("%Y-%m-%d_%H-%M-%S") != time.strftime("%Y-%m-%d_%H-%M-%S"):
            if self.currtime != '':
                self.writeData()
                self.clearData()
            self.currtime = time

        for key, value in data.iteritems():
            if key in self.data:
                tmp = self.data[key]
                tmp = max(tmp, value)
                self.data[key] = tmp
            else:
                self.data[key] = value

    def writeData(self):
        fileName = self.datafolder + os.sep + 'data_' + self.currtime.strftime("%Y-%m-%d_%H") + '.json'
        logging.info('Write Data : ' + str(self.currtime) + " to " + fileName)

        data = {}
        if os.path.exists(fileName):
            with open(fileName, 'r') as fileHandle:
                txt = fileHandle.read()
                if len(txt) != 0:
                    data = json.loads(txt)

        data[self.currtime.strftime("%Y-%m-%d_%H-%M-%S")] = self.data
        with open(fileName, 'w') as fileHandle:
            fileHandle.write(json.dumps(data))

    def clearData(self):
        self.currtime = self.getCurrTime()
        self.data = {}

    def getCurrTime(self):
        time = getCurrentTime()
        seconds = int(time.second / 10)
        return time.replace(second=seconds*10, microsecond=0)

    def writeSchedule(self, time):
        fileName = self.schedulefolder + os.sep + 'schedule_' + time.replace(':', '-')
        logging.info('Write Schedule : ' + fileName)

        with open(fileName, 'w') as fileHandle:
            fileHandle.write(time)

    def existSchedule(self, time):
        fileName = self.schedulefolder + os.sep + 'schedule_' + time.replace(':', '-')
        return os.path.exists(fileName)

    def getManuelSchedule(self):
        fileName = self.folder + os.sep + 'manuelSchedule'
        if os.path.exists(fileName):
            return fileName


#
#  class: Schedule
#  The Schedule class checks, if a configured schedule
#  is active.
#
class Schedule:
    def __init__(self, store, com):
        self.store = store
        self.com = com

    def do(self):
        curTime = getCorrectedTime()
        # execute time schedule
        if curTime.strftime('%H:%M') in config.getSchedules():
            self.processSchedule(curTime.strftime('%Y-%m-%d_%H-%M'))
        # execute manuel schedule
        manuelSchedule = self.store.getManuelSchedule()
        if not manuelSchedule is None:
            os.remove(manuelSchedule)
            self.processSchedule(curTime.strftime('%Y-%m-%d_%H-%M'))

    def processSchedule(self, name):
        if not self.store.existSchedule(name):
            logging.info('Schedule ' + name + ' is active')
            self.com.write('Pump=1')
            store.writeSchedule(name)


def run():
    global com
    global store

    # load configuration
    outputFolder = config.getOutputFolder()
    device = config.getDevice()
    createFolder(outputFolder)

    #confgure logging
    logfile = outputFolder + os.sep + 'watcher.log'
    print 'Logfile: ' + logfile
    logging.basicConfig(filename=logfile, level=logging.DEBUG, format='%(levelname)s-%(asctime)s-%(name)s - %(message)s')

    logging.info('Output-Folder: ' + outputFolder)
    logging.info('Device       : ' + device)

    # Startup
    #com = Communicator(device)
    com = SerialCommunicator(device)
    store = DataStore(outputFolder)
    schedule = Schedule(store, com)

    # synchronize time
    defineTimeDiff()
    logging.info('Old       Time: ' + str(getCurrentTime()))
    logging.info('Corrected Time: ' + str(getCorrectedTime()))

    # start processing
    while True:
        startTime = time.time()

        try:
            data = com.read()
            store.addData(data)
            schedule.do()
        except Exception as expt:
            logging.error('An error occurs during processing data: ' + str(expt.message))
            logging.error(expt)
            store.clearData()

        # reducing speed of the processing loop
        durationTime = time.time() - startTime
        if durationTime < 1:
            time.sleep(1)
