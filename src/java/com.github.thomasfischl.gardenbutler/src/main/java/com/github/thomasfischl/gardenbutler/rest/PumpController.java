package com.github.thomasfischl.gardenbutler.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.thomasfischl.gardenbutler.domain.ActorAction;
import com.github.thomasfischl.gardenbutler.rest.dto.HistoricalActorActionDataDTO;
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
    result.add(linkTo(methodOn(PumpController.class).execute("pump1", "activate", 5000)).withRel("pump1-activate"));
    result.add(linkTo(methodOn(PumpController.class).execute("pump2", "activate", 5000)).withRel("pump2-activate"));
    result.add(linkTo(methodOn(PumpController.class).getHistroy("pump1")).withRel("history"));
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.POST)
  @ResponseBody
  public HttpEntity<?> execute(@PathVariable String id, @RequestParam String action, @RequestParam long duration) {
    switch (action) {
    case "activate":
      storeService.queueActorAction(new ActorAction(id, "activate", String.valueOf(duration), "Triggered by user."));
      break;
    default:
      return new ResponseEntity<>("Invalid action parameter", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/history", method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<HistoricalActorActionDataDTO> getHistroy(@PathVariable String id) {
    if (id == null || id.isEmpty()) {
      throw new IllegalArgumentException("The id can't be null or empty.");
    }

    HistoricalActorActionDataDTO result = new HistoricalActorActionDataDTO();
    Collection<ActorAction> objects = storeService.getActorActionStore().loadAll();
    for (ActorAction a : objects) {
      if (id.equals(a.getActorName())) {
        result.add(result.new ActorActionDTO(a.getActorName(), a.getAction(), a.getParam(), a.getExecutionTime(), a.getDescription(), a.getId()));
      }
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

}
