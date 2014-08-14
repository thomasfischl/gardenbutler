package com.github.thomasfischl.gardenbutler.client.rest;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

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

  private boolean initialized;

  private Map<String, String> urls = new HashMap<>();

  public RestClient() {
    template = new RestTemplate();
  }

  public SensorDataListDTO getCurrentSensorValues() {
    try {
      ResponseEntity<SensorDataListDTO> entity = template.getForEntity(urls.get("sensors"), SensorDataListDTO.class);
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

  public HistoricalSensorDataDTO getHistroyForSensor(String name) {
    return template.getForEntity(urls.get(name + "-history"), HistoricalSensorDataDTO.class).getBody();
  }

  public HistoricalActorActionDataDTO getHistroyForPump(String name) {
    return template.getForEntity(urls.get(name + "-history"), HistoricalActorActionDataDTO.class).getBody();
  }

  private SensorDataListDTO parseResult() {
    ResponseEntity<String> entity = template.getForEntity(urls.get("sensors"), String.class);

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

      urls.put("sensors", entity.getBody().getLink("sensors").getHref());
      urls.put("pumps", entity.getBody().getLink("pump").getHref());
      getCurrentSensorValues().getData().forEach(obj -> urls.put(obj.getName() + "-history", URLDecoder.decode(obj.getLink("history").getHref())));

      ResponseEntity<ResourceSupport> pumpResp = template.getForEntity(urls.get("pumps"), ResourceSupport.class);
      urls.put("pump1-history", pumpResp.getBody().getLink("history").getHref());
      urls.put("pump2-history", pumpResp.getBody().getLink("history").getHref()); // FIXME fix rest service
      urls.put("pump1-activate", pumpResp.getBody().getLink("pump1-activate").getHref());
      urls.put("pump2-activate", pumpResp.getBody().getLink("pump2-activate").getHref());
    }
    initialized = true;
  }

  public boolean isInitialized() {
    return initialized;
  }

}
