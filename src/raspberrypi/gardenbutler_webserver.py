#!/usr/bin/env python
#

import BaseHTTPServer
import SimpleHTTPServer
import communication_config as gconfig
import gardenbutler_dataprocessor as gprocessor
import os


def loadData(dataHeader, dataRows):
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


class RequestHandler(SimpleHTTPServer.SimpleHTTPRequestHandler):

    def do_GET(self):
        print 'Client: ' + self.client_address[0]

        dataHeaders = []
        dataRows = []
        loadData(dataHeaders, dataRows)

        self.wfile.write('<html><body><table border="1"><tr>')
        for header in dataHeaders:
            self.wfile.write('<th>' + str(header) + '</th>')
        self.wfile.write('</tr>')

        for row in dataRows:
            self.wfile.write('<tr>')
            for col in row:
                self.wfile.write('<td>' + str(col) + '</td>')
            self.wfile.write('</tr>')

        self.wfile.write('</table></body></html>')


def start_server():
    server_address = ("", 8000)
    server = BaseHTTPServer.HTTPServer(server_address, RequestHandler)
    server.serve_forever()

start_server()