package com.github.thomasfischl.gardenbutler.microcontroller;

public interface IMicrocontrollerBridge {

  void init();

  void activatePump1(long duration);

  void activatePump2(long duration);

  void readData();

  Double getSensorValue(String sensorName);

}
