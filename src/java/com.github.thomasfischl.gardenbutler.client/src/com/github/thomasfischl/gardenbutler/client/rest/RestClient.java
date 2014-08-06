package com.github.thomasfischl.gardenbutler.client.rest;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClient {

  private RestTemplate template;

  private Link sensorsLink;

  private boolean initialized;

  public RestClient() {
    template = new RestTemplate();
  }

  public SensorDataListDTO getCurrentSensorValues() {
    ResponseEntity<SensorDataListDTO> entity = template.getForEntity(sensorsLink.getHref(), SensorDataListDTO.class);
    if (entity.hasBody()) {
      return entity.getBody();
    }
    return null;
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
