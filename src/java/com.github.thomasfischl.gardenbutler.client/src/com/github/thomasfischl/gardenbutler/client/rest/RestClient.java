package com.github.thomasfischl.gardenbutler.client.rest;

import java.net.URLDecoder;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Component
public class RestClient {

  private RestTemplate template;

  private Link sensorsLink;

  private boolean initialized;

  public RestClient() {
    template = new RestTemplate();
  }

  public SensorDataListDTO getCurrentSensorValues() {
    try {
      ResponseEntity<SensorDataListDTO> entity = template.getForEntity(sensorsLink.getHref(), SensorDataListDTO.class);
      if (entity.hasBody()) {
        return entity.getBody();
      }
    } catch (Exception e) {
      try {
        return parseResult();
      } catch (Exception e1) {
        System.out.println("First: -----------------------------------------------------------");
        e.printStackTrace();
        System.out.println("Second: ----------------------------------------------------------");
        e1.printStackTrace();
      }
    }

    return new SensorDataListDTO();
  }

  public HistoricalSensorDataDTO getHistroyForSensor(SensorDataDTO dto) {

    Link historyLink = dto.getLink("history");
    if (historyLink != null) {
      ResponseEntity<HistoricalSensorDataDTO> entity = template.getForEntity(URLDecoder.decode(historyLink.getHref()), HistoricalSensorDataDTO.class);
      return entity.getBody();
    }

    return null;
  }

  private SensorDataListDTO parseResult() {
    ResponseEntity<String> entity = template.getForEntity(sensorsLink.getHref(), String.class);

    JsonParser parser = new JsonParser();
    JsonElement root = parser.parse(entity.getBody());
    JsonArray dataList = root.getAsJsonArray();

    SensorDataListDTO result = new SensorDataListDTO();
    Gson gson = new Gson();
    for (int i = 0; i < dataList.size(); i++) {
      JsonElement data = dataList.get(i);
      result.addData(gson.fromJson(data, SensorDataDTO.class));
    }

    return result;
  }

  public void init(String url) {
    ResponseEntity<ResourceSupport> entity = template.getForEntity(url + "/rest", ResourceSupport.class);

    if (entity.hasBody()) {
      for (Link link : entity.getBody().getLinks()) {
        System.out.println(link.getRel() + " => " + link.getHref());
      }

      sensorsLink = entity.getBody().getLink("sensors");
    }
    initialized = true;
  }

  public boolean isInitialized() {
    return initialized;
  }

}
