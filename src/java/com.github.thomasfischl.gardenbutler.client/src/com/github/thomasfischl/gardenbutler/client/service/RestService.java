package com.github.thomasfischl.gardenbutler.client.service;

import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.thomasfischl.gardenbutler.client.MainPane;
import com.github.thomasfischl.gardenbutler.client.rest.HistoricalActorActionDataDTO;
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
    Platform.runLater(() -> {
      for (SensorDataDTO data : dataList.getData()) {
        controller.update(data);
      }
    });

  }

  public List<String> getSensorNames() {
    return client.getCurrentSensorValues().getData().stream().map(obj -> obj.getName()).collect(Collectors.toList());
  }

  public HistoricalSensorDataDTO getHistoricalSensorData(String sensorName) {
    return client.getHistroyForSensor(sensorName);
  }

  public HistoricalActorActionDataDTO getHistroyForPump(String sensorName) {
    return client.getHistroyForPump(sensorName);
  }

  public void setController(MainPane controller) {
    this.controller = controller;
  }

}
