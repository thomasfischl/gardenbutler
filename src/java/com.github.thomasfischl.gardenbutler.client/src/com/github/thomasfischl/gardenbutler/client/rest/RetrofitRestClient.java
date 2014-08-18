package com.github.thomasfischl.gardenbutler.client.rest;

import java.util.List;
import java.util.stream.Collectors;

import retrofit.RestAdapter;

public class RetrofitRestClient implements IRestClient {

  private boolean initialized;

  private SensorService service;

  @Override
  public SensorDataListDTO getCurrentSensorValues() {
    return service.getSensors();
  }

  @Override
  public List<String> getSensorNames() {
    return getCurrentSensorValues().getData().stream().map(obj -> obj.getName()).collect(Collectors.toList());
  }

  @Override
  public HistoricalSensorDataDTO getHistroyForSensor(String name) {
    return service.getSensorHistroy(name);
  }

  @Override
  public HistoricalActorActionDataDTO getHistroyForPump(String name) {
    return service.getPumpHistory(name);
  }

  @Override
  public void activatePump(String name) {
    service.activatePump(name, 5000);
  }

  @Override
  public void init(String url) {
    RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(url).build();
    service = restAdapter.create(SensorService.class);

    initialized = true;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

}
