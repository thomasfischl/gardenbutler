package com.github.thomasfischl.gardenbutler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.thomasfischl.gardenbutler.Configuration;
import com.github.thomasfischl.gardenbutler.domain.ActorAction;
import com.github.thomasfischl.gardenbutler.microcontroller.PiMicorcontroller;

@Service
public class MicrocontrollerService {

  @Autowired
  private StoreService storeService;

  @Autowired
  private Configuration config;

  @Autowired
  private PiMicorcontroller microcontroller;

  // every 10 seconds
  @Scheduled(fixedRate = 10000)
  public void readData() {
    System.out.println("Reading data from microcontroller ...");

    long time = System.currentTimeMillis();

    microcontroller.readData();

    for (String sensorName : config.getSensorNameList()) {
      Double value = microcontroller.getSensorValue(sensorName);
      if (value != null) {
        storeService.storeSensorData(sensorName, value, time);
      }
    }
    // TODO implement me
  }

  // every 10 seconds
  @Scheduled(fixedRate = 10000)
  public void executeAction() {
    System.out.println("Send action to microcontroller ...");

    ActorAction action = storeService.nextActorAction();
    if (action == null) {
      return;
    }

    if ("activate".equalsIgnoreCase(action.getAction())) {
      long duration = Long.valueOf(action.getParam());
      if ("pump1".equalsIgnoreCase(action.getActorName())) {
        microcontroller.activatePump1(duration);
      } else if ("pump2".equalsIgnoreCase(action.getActorName())) {
        microcontroller.activatePump2(duration);
      }
    }

    System.out.println("Execute action " + action.getAction() + " on actor " + action.getActorName() + " with parameter (" + action.getParam() + ")");
    // TODO implement me

    microcontroller.test();
  }
}
