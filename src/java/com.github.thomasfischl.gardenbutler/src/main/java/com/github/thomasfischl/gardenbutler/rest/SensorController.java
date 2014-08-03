package com.github.thomasfischl.gardenbutler.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.thomasfischl.gardenbutler.domain.SensorData;
import com.github.thomasfischl.gardenbutler.rest.dto.HistoricalSensorDataDTO;
import com.github.thomasfischl.gardenbutler.rest.dto.SensorDataDTO;
import com.github.thomasfischl.gardenbutler.service.StoreService;

@Controller
@RequestMapping("/rest/sensor")
public class SensorController {

  @Autowired
  private StoreService sensorStoreService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<List<SensorDataDTO>> getSensorData() {

    List<SensorDataDTO> result = new ArrayList<SensorDataDTO>();
    for (SensorData sensor : sensorStoreService.loadCurrentSensorData()) {
      SensorDataDTO dto = new SensorDataDTO(sensor.getName(), sensor.getValue());
      dto.add(linkTo(methodOn(SensorController.class).getSensorHistroy(sensor.getName())).withRel("history"));
      result.add(dto);
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(value = "/{sensorName}/history", method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<HistoricalSensorDataDTO> getSensorHistroy(@PathVariable String sensorName) {
    Map<Long, Double> data = sensorStoreService.loadHistoricalSensorValues(sensorName);
    return new ResponseEntity<>(new HistoricalSensorDataDTO(sensorName, data), HttpStatus.OK);
  }

}
