package com.github.thomasfischl.gardenbutler.client.rest;

public class SensorDataDTO {

  private String name;

  private double value;

  public SensorDataDTO(String name, double value) {
    super();
    this.name = name;
    this.value = value;
  }

  public SensorDataDTO() {
  }

  public String getName() {
    return name;
  }

  public double getValue() {
    return value;
  }

}
