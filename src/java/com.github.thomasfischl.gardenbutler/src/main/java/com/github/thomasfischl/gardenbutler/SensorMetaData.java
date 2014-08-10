package com.github.thomasfischl.gardenbutler;

public class SensorMetaData {

  public final String displayName;

  public final String hardwareId;

  public SensorMetaData(String displayName, String hardwareId) {
    super();
    this.displayName = displayName;
    this.hardwareId = hardwareId;
  }

  @Override
  public String toString() {
    return displayName + "(" + hardwareId + ")";
  }
}
