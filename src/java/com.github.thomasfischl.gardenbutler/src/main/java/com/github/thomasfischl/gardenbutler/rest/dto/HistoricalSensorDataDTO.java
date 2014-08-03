package com.github.thomasfischl.gardenbutler.rest.dto;

import java.util.Map;

import org.springframework.hateoas.ResourceSupport;

public class HistoricalSensorDataDTO extends ResourceSupport {

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
