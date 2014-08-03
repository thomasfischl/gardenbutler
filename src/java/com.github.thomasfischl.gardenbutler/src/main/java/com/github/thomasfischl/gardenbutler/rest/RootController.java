package com.github.thomasfischl.gardenbutler.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.thomasfischl.gardenbutler.service.StoreService;

@Controller
@RequestMapping("/rest")
public class RootController {

  @Autowired
  private StoreService sensorStoreService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<ResourceSupport> getSensorData() {
    ResourceSupport result = new ResourceSupport();
    result.add(linkTo(PumpController.class).withRel("pump"));
    result.add(linkTo(SensorController.class).withRel("sensors"));
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

}
