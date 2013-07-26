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
import WatcherConfig as config
import logging


#
# functions
#
def getCurrentTime():
    return datetime.datetime.now()


def getCorrectedTime():
    return getCurrentTime() + datetime.timedelta(seconds=timeDiff)


def getTimeDiff():
    #response = urllib2.urlopen('http://api.timezonedb.com/?zone=Europe/Vienna&key=SJ1QUOEAUM26&format=json')
    response = urllib2.urlopen('http://wwp.greenwichmeantime.com/time/scripts/clock-8/x.php')
    content = response.read()
    #jsoData = json.loads(str(content))
    #newTime = jsoData['timestamp']
    #print content
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
        line = 'T1=1;T2=2;Pump=0'
        result = {}
        for value in line.split(';'):
            if '=' in value:
                data = value.split('=')
                result[data[0]] = data[1]
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
        result = {}
        for value in line.split(';'):
            if '=' in value:
                data = value.split('=')
                result[data[0]] = data[1]
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
    currtime = ''

    def __init__(self, folder):
        self.folder = folder
        self.datafolder = folder + os.sep + 'data'
        self.schedulefolder = folder + os.sep + 'schedule'
        if not os.path.exists(self.folder):
            os.makedirs(self.folder)
        if not os.path.exists(self.datafolder):
            os.makedirs(self.datafolder)
        if not os.path.exists(self.schedulefolder):
            os.makedirs(self.schedulefolder)

    def addData(self, data):
        print 'add data ...'
        time = self.getCurrTime()
        if self.currtime != time:
            if self.currtime != '':
                self.writeData()
            self.currtime = time

        for key, value in data.iteritems():
            if key in self.data:
                tmp = self.data[key]
                tmp = max(tmp, value)
                self.data[key] = tmp
            else:
                self.data[key] = value

    def writeData(self):
        fileName = self.datafolder + os.sep + 'data_' + self.currtime + '.json'
        logging.info('Write Data : ' + self.currtime + " to " + fileName)

        with open(fileName, 'w') as fileHandle:
            fileHandle.write(json.dumps({'Name': self.currtime, 'Property': self.data}))

    def clearData(self):
        self.currtime = self.getCurrTime()

    def getCurrTime(self):
        return getCorrectedTime().strftime("%Y-%m-%d_%H-%M") + '-' + str(int(getCorrectedTime().second / 10))

    def writeSchedule(self, time):
        fileName = self.schedulefolder + os.sep + 'schedule_' + time.replace(':', '-')
        logging.info('Write Schedule : ' + fileName)

        with open(fileName, 'w') as fileHandle:
            fileHandle.write(time)

    def existSchedule(self, time):
        fileName = self.schedulefolder + os.sep + 'schedule_' + time.replace(':', '-')
        return os.path.exists(fileName)


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
        if curTime.strftime('%H:%M') in config.schedules:
            s = curTime.strftime('%Y-%m-%d_%H-%M')
            if not self.store.existSchedule(s):
                logging.info('Schedule ' + s + ' is active')
                self.com.write('pumpOn')
                store.writeSchedule(s)


def run():
    global com
    global store

    # load configuration
    outputFolder = config.outputFolder
    device = config.device

    #confgure logging
    logfile = outputFolder + os.sep + 'watcher.log'
    logging.basicConfig(filename=logfile, level=logging.DEBUG, format='%(levelname)s-%(asctime)s-%(name)s - %(message)s')

    logging.info('Output-Folder: ' + outputFolder)
    logging.info('Device       : ' + device)

    # Startup
    com = Communicator(device)
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