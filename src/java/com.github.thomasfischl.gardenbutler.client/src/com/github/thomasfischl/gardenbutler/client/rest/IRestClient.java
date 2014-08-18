package com.github.thomasfischl.gardenbutler.client.rest;

import java.util.List;

public interface IRestClient {

  void init(String url);

  boolean isInitialized();

  void activatePump(String name);

  HistoricalActorActionDataDTO getHistroyForPump(String name);

  HistoricalSensorDataDTO getHistroyForSensor(String name);

  List<String> getSensorNames();

  SensorDataListDTO getCurrentSensorValues();

}
