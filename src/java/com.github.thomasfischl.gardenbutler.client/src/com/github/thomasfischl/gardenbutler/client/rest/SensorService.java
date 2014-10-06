package com.github.thomasfischl.gardenbutler.client.rest;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface SensorService {

  @GET("/rest/sensor/{name}/history?timeFrame=oneday")
  HistoricalSensorDataDTO getSensorHistroy(@Path("name") String sensorName);

  @GET("/rest/sensor")
  SensorDataListDTO getSensors();

  @GET("/rest/pump/{name}/history")
  HistoricalActorActionDataDTO getPumpHistory(@Path("name") String name);

  @POST("/rest/pump/{name}?action=activate&duration=5000")
  String activatePump5000(@Path("name") String name);

}
