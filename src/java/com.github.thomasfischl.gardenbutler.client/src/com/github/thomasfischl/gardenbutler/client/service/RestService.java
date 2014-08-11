package com.github.thomasfischl.gardenbutler.client.service;

import javafx.application.Platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.thomasfischl.gardenbutler.client.MainPane;
import com.github.thomasfischl.gardenbutler.client.rest.HistoricalSensorDataDTO;
import com.github.thomasfischl.gardenbutler.client.rest.RestClient;
import com.github.thomasfischl.gardenbutler.client.rest.SensorDataDTO;
import com.github.thomasfischl.gardenbutler.client.rest.SensorDataListDTO;

@Service
public class RestService {

  @Autowired
  private RestClient client;

  private MainPane controller;

  @Scheduled(fixedRate = 2000)
  public void fetachSensorData() {
    if (controller == null || !client.isInitialized()) {
      return;
    }

    SensorDataListDTO dataList = client.getCurrentSensorValues();
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        for (SensorDataDTO data : dataList.getData()) {
          controller.update(data);
        }
      }
    });

  }
  
  public HistoricalSensorDataDTO getHistoricalSensorData(SensorDataDTO dto){
    return client.getHistroyForSensor(dto);
  }

  public void setController(MainPane controller) {
    this.controller = controller;
  }

}
