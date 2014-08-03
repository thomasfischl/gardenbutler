package com.github.thomasfischl.gardenbutler.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MicrocontrollerComService {

  @Autowired
  private StoreService storeService;

  private Random rand;

  public MicrocontrollerComService() {
    rand = new Random();
  }

  // every 10 seconds
  @Scheduled(fixedRate = 10000)
  public void readData() {
    System.out.println("Reading data from microcontroller ...");

    long time = System.currentTimeMillis();
    storeService.storeSensorData("Temperature1", 5 + rand.nextInt(20), time);
    storeService.storeSensorData("Temperature2", 5 + rand.nextInt(20), time);
    storeService.storeSensorData("Temperature3", 5 + rand.nextInt(20), time);
    storeService.storeSensorData("Temperature4", 5 + rand.nextInt(20), time);
  }

}
