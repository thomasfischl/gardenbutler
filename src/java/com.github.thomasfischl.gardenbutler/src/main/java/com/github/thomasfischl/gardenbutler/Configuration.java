package com.github.thomasfischl.gardenbutler;

import java.util.ArrayList;
import java.util.List;

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
