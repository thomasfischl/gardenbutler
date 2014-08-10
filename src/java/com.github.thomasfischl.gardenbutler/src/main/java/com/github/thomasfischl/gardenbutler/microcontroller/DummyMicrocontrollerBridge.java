package com.github.thomasfischl.gardenbutler.microcontroller;

import java.util.Random;

public class DummyMicrocontrollerBridge implements IMicrocontrollerBridge {

  private Random rand = new Random();

  @Override
  public void init() {
  }

  @Override
  public void activatePump1(long duration) {
  }

  @Override
  public void activatePump2(long duration) {
  }

  @Override
  public void readData() {
  }

  @Override
  public Double getSensorValue(String sensorName) {
    return (double) 10 + rand.nextInt(30);
  }

}
