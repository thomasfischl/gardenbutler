package com.github.thomasfischl.gardenbutler.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.thomasfischl.gardenbutler.Configuration;
import com.github.thomasfischl.gardenbutler.domain.ActorAction;

@Service
public class MicrocontrollerComService {

  @Autowired
  private StoreService storeService;

  @Autowired
  private Configuration config;

  private Random rand;

  public MicrocontrollerComService() {
    rand = new Random();
  }

  // every 10 seconds
  @Scheduled(fixedRate = 10000)
  public void readData() {
    System.out.println("Reading data from microcontroller ...");

    long time = System.currentTimeMillis();

    for (String sensorName : config.getSensorNameList()) {
      storeService.storeSensorData(sensorName, 5 + rand.nextInt(20), time);
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

    System.out.println("Execute action " + action.getAction() + " on actor " + action.getActorName() + " with parameter (" + action.getParam() + ")");
    // TODO implement me

  }
}
