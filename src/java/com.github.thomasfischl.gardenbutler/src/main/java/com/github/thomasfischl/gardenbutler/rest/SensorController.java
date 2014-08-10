package com.github.thomasfischl.gardenbutler.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.thomasfischl.gardenbutler.Average;
import com.github.thomasfischl.gardenbutler.domain.SensorData;
import com.github.thomasfischl.gardenbutler.rest.dto.HistoricalSensorDataDTO;
import com.github.thomasfischl.gardenbutler.rest.dto.SensorDataDTO;
import com.github.thomasfischl.gardenbutler.rest.dto.SensorDataListDTO;
import com.github.thomasfischl.gardenbutler.service.StoreService;

@Controller
@RequestMapping("/rest/sensor")
public class SensorController {

  @Autowired
  private StoreService sensorStoreService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<SensorDataListDTO> getSensorData() {

    SensorDataListDTO result = new SensorDataListDTO();
    for (SensorData sensor : sensorStoreService.loadCurrentSensorData()) {
      SensorDataDTO dto = new SensorDataDTO(sensor.getName(), sensor.getValue());
      dto.add(linkTo(methodOn(SensorController.class).getSensorHistroy(sensor.getName(), "oneday")).withRel("history"));
      result.addData(dto);
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(value = "/{sensorName}/history", method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<HistoricalSensorDataDTO> getSensorHistroy(@PathVariable String sensorName, @RequestParam(required = false) String timeFrame) {
    Map<Long, Double> data = sensorStoreService.loadHistoricalSensorValues(sensorName);

    if ("oneday".equals(timeFrame)) {
      Map<Long, Average> temp = new LinkedHashMap<>();

      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.MILLISECOND, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);

      temp.put(cal.getTimeInMillis(), new Average());
      for (int i = 0; i < 23; i++) {
        cal.add(Calendar.HOUR_OF_DAY, -1);
        temp.put(cal.getTimeInMillis(), new Average());
      }

      for (Entry<Long, Double> entry : data.entrySet()) {
        for (long time : temp.keySet()) {
          if (time < entry.getKey() && entry.getKey() < (time + TimeUnit.HOURS.toMillis(1))) {
            temp.get(time).add(entry.getValue());
          }
        }
      }

      data.clear();
      for (Entry<Long, Average> entry : temp.entrySet()) {
        data.put(entry.getKey(), entry.getValue().getAvg());
      }

    }

    return new ResponseEntity<>(new HistoricalSensorDataDTO(sensorName, data), HttpStatus.OK);
  }

}
