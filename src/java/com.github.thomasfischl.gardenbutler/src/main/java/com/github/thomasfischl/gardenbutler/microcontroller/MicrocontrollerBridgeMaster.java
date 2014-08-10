package com.github.thomasfischl.gardenbutler.microcontroller;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class MicrocontrollerBridgeMaster implements IMicrocontrollerBridge {

  private IMicrocontrollerBridge controller;

  private boolean initialized;

  @Override
  @PostConstruct
  public void init() {
    if (!initialized) {
      if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        controller = new DummyMicrocontrollerBridge();
      } else {
        controller = new PiMicorcontroller();
      }
      controller.init();
      initialized = true;
    }
  }

  @Override
  public void activatePump1(long duration) {
    controller.activatePump1(duration);
  }

  @Override
  public void activatePump2(long duration) {
    controller.activatePump2(duration);
  }

  @Override
  public void readData() {
    controller.readData();
  }

  @Override
  public Double getSensorValue(String sensorName) {
    return controller.getSensorValue(sensorName);
  }

}
