package com.github.thomasfischl.gardenbutler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.thomasfischl.gardenbutler.service.MicrocontrollerService;
import com.google.common.base.Splitter;

@Component
public class Configuration {

  private static final Logger LOG = LoggerFactory.getLogger(MicrocontrollerService.class);

  @Value("${database.file}")
  private String databaseFilePath;

  @Value("${sensor.names}")
  private String sensorNames;

  private List<SensorMetaData> sensorNameList;

  @PostConstruct
  private void init() {
    LOG.info("----------------------------------------------------");
    LOG.info("-  GardenButler v2 Configuartion                   -");
    LOG.info("----------------------------------------------------");
    LOG.info("- Database File: " + getDatabaseFilePath());

    List<SensorMetaData> sensors = getSensorNameList();
    for (int i = 0; i < sensors.size(); i++) {
      LOG.info("- Sensor " + i + ": " + sensors.get(i));
    }
    LOG.info("----------------------------------------------------");
  }

  public String getDatabaseFilePath() {
    return databaseFilePath;
  }

  public List<SensorMetaData> getSensorNameList() {
    if (sensorNameList == null) {
      sensorNameList = new ArrayList<>();
      if (sensorNames != null) {
        Map<String, String> data = Splitter.on(";").trimResults().withKeyValueSeparator("=>").split(sensorNames);
        for (Entry<String, String> sensor : data.entrySet()) {
          sensorNameList.add(new SensorMetaData(sensor.getValue(), sensor.getKey()));
        }
      }
    }
    return sensorNameList;
  }
}
