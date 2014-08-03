package com.github.thomasfischl.gardenbutler.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.thomasfischl.gardenbutler.domain.ActorAction;
import com.github.thomasfischl.gardenbutler.rest.dto.HistoricalSensorDataDTO;
import com.github.thomasfischl.gardenbutler.service.StoreService;

@Controller
@RequestMapping("/rest/pump")
public class PumpController {

  @Autowired
  private StoreService storeService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<ResourceSupport> getSensorData() {
    ResourceSupport result = new ResourceSupport();
    result.add(linkTo(methodOn(PumpController.class).execute("activate", 10)).withRel("activate"));
    result.add(linkTo(methodOn(PumpController.class).getHistroy()).withRel("history"));
    result.add(linkTo(methodOn(PumpController.class).getSchedules()).withRel("schedules"));
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @ResponseBody
  public HttpEntity<?> execute(@RequestParam String action, @RequestParam long duration) {
    switch (action) {
    case "activate":
      storeService.queueActorAction(new ActorAction("pump", "activate", String.valueOf(duration)));
      break;
    default:
      return new ResponseEntity<>("Invalid action parameter", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/history", method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<HistoricalSensorDataDTO> getHistroy() {
    return null;
  }

  @RequestMapping(value = "/schedule", method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<HistoricalSensorDataDTO> getSchedules() {
    return null;
  }

}
