package com.github.thomasfischl.gardenbutler.client.rest;

import java.util.Map;

public class HistoricalSensorDataDTO {

  private String name;

  private Map<Long, Double> historicalData;

  public HistoricalSensorDataDTO(String name, Map<Long, Double> historicalData) {
    super();
    this.name = name;
    this.historicalData = historicalData;
  }

  public HistoricalSensorDataDTO() {
  }

  public String getName() {
    return name;
  }

  public Map<Long, Double> getHistoricalData() {
    return historicalData;
  }

}
