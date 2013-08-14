#
# This file is a configuration file for the
# Watcher script.
#
import os

_schedules = {}
_outputFolder = {}
_device = {}

#environment = 'bigmama'
environment = 'pi'

_schedules['bigmama'] = ['17:23', '17:25', '17:26']
_schedules['pi'] = ['10:01', '13:01', '15:01', '21:10']

_outputFolder['bigmama'] = 'C:\\temp\\watcher'
_outputFolder['pi'] = '/home/pi/watcher'

_device['bigmama'] = 'COM3'
_device['pi'] = '/dev/ttyACM0'


def getSchedules():
    return _schedules[environment]


def getOutputFolder():
    return _outputFolder[environment]


def getDataFolder(outputFolder=getOutputFolder()):
    return outputFolder + os.sep + 'data'


def getScheduleFolder(outputFolder=getOutputFolder()):
    return outputFolder + os.sep + 'schedule'


def getActionFolder(outputFolder=getOutputFolder()):
    return outputFolder + os.sep + 'action'


def getDevice():
    return _device[environment]
