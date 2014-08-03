package com.github.thomasfischl.gardenbutler.domain;

public class SensorData {
  private String name;

  private double value;

  public SensorData(String name, double value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public double getValue() {
    return value;
  }
}
