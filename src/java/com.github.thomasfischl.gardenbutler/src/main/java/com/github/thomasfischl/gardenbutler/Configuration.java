package com.github.thomasfischl.gardenbutler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;

@Component
public class Configuration {

  @Value("${database.file}")
  private String databaseFilePath;

  @Value("${sensor.names}")
  private String sensorNames;

  private List<String> sensorNameList;

  @PostConstruct
  private void init() {
    System.out.println("----------------------------------------------------");
    System.out.println("-  GardenButler v2 Configuartion                   -");
    System.out.println("----------------------------------------------------");
    System.out.println("- Database File: " + getDatabaseFilePath());

    List<String> sensors = getSensorNameList();
    for (int i = 0; i < sensors.size(); i++) {
      System.out.println("- Sensor " + i + ": " + sensors.get(i));
    }
    System.out.println("----------------------------------------------------");
  }

  public String getDatabaseFilePath() {
    return databaseFilePath;
  }

  public List<String> getSensorNameList() {
    if (sensorNameList == null) {
      sensorNameList = new ArrayList<String>();
      if (sensorNames != null) {
        sensorNameList.addAll(Splitter.on(";").trimResults().splitToList(sensorNames));
      }
    }
    return sensorNameList;
  }
}
