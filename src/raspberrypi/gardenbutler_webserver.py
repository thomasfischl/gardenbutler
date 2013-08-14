#!/usr/bin/env python
#

import BaseHTTPServer
import SimpleHTTPServer
import communication_config as gconfig
import gardenbutler_dataprocessor as gprocessor
import os
import datetime


def loadMeasureData(dataHeader, dataRows):
    dataFolder = gconfig.getDataFolder()
    dataFiles = [f for f in os.listdir(dataFolder) if os.path.isfile(dataFolder + os.sep + f) and f.endswith(".json")]

    tmpRows = []
    gprocessor.readData(dataFolder, dataFiles, dataHeader, tmpRows)
    tmpRows.sort(key=lambda x: x[0])
    gprocessor.aggregateRows(dataHeader, tmpRows, dataRows)

    # format data output and remove minute information
    for row in dataRows:
        if len(row) > 0:
            tmp = row[0].split('_')
            row[0] = tmp[0] + ' ' + tmp[1].replace('-', ':')[:-3]


def writeMeasureData(resp):
    resp += '<h2> Measure Values </h2><ul>'
    dataHeaders = []
    dataRows = []
    loadMeasureData(dataHeaders, dataRows)

    resp += '<table border="1"><tr>'
    for header in dataHeaders:
        resp += '<th>' + str(header) + '</th>'
    resp += '</tr>'

    for row in dataRows:
        resp += '<tr>'
        for col in row:
            resp += '<td>' + str(col) + '</td>'
        resp += '</tr>'
    resp += '</table>'
    return resp


def writeScheduleData(resp):
    resp += '<h2> Defined Schedules </h2><ul>'
    for schedule in gconfig.getSchedules():
        resp += '<li> Daily: '+schedule+'</li>'
    resp += '</ul>'
    return resp


def writeScheduleHistory(resp):
    resp += '<h2> Schedule History </h2><ul>'

    folder = gconfig.getScheduleFolder()
    schedules = [f for f in os.listdir(folder) if os.path.isfile(folder + os.sep + f) and f.startswith('schedule_')]

    for schedule in schedules:
        resp += '<li> '+schedule+'</li>'
    resp += '</ul>'
    return resp


class RequestHandler(SimpleHTTPServer.SimpleHTTPRequestHandler):

    def do_GET(self):
        print 'Client: ' + self.client_address[0] + ' ' + self.path

        resp = ''
        resp += '<html><body>'
        resp += '<h1>GardenButler 2.0 (' + datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S") + ')</h1>'

        resp = writeScheduleData(resp)
        resp = writeScheduleHistory(resp)
        resp = writeMeasureData(resp)

        resp += '</body></html>'
        self.wfile.write(resp)


def start_server():
    server_address = ("", 8000)
    server = BaseHTTPServer.HTTPServer(server_address, RequestHandler)
    server.serve_forever()

start_server()