package com.github.thomasfischl.gardenbutler.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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

import com.github.thomasfischl.gardenbutler.domain.ActorSchedule;
import com.github.thomasfischl.gardenbutler.rest.dto.ScheduleDTO;
import com.github.thomasfischl.gardenbutler.service.StoreService;

@Controller
@RequestMapping("/rest/schedule")
public class ScheduleController {

  @Autowired
  private StoreService storeService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<ResourceSupport> getSensorData() {
    ResourceSupport result = new ResourceSupport();
    for (ActorSchedule schedule : storeService.getActorSchedulesStore().loadAll()) {
      result.add(linkTo(methodOn(ScheduleController.class).getSchedule(schedule.getId())).withRel("schedule-" + schedule.getId()));
    }
    result.add(linkTo(methodOn(ScheduleController.class).store("pump1", "activate", "1000", "* * * * *")).withRel("store"));
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<ScheduleDTO> getSchedule(@PathVariable long id) {
    ActorSchedule obj = storeService.getActorSchedulesStore().load(id);
    return new ResponseEntity<>(convert(obj), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public HttpEntity<Object> delete(@PathVariable long id) {
    storeService.getActorSchedulesStore().remove(id);
    return new ResponseEntity<Object>(HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @ResponseBody
  public HttpEntity<ScheduleDTO> store(@RequestParam String actorName, @RequestParam String action, @RequestParam String param, @RequestParam String cronExpr) {
    ActorSchedule obj = new ActorSchedule(actorName, action, param, cronExpr);
    storeService.getActorSchedulesStore().store(obj);

    ScheduleDTO result = convert(obj);
    result.add(linkTo(methodOn(ScheduleController.class).getSchedule(obj.getId())).withSelfRel());
    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  private ScheduleDTO convert(ActorSchedule obj) {
    return new ScheduleDTO(obj.getId(), obj.getActorName(), obj.getAction(), obj.getParam(), obj.getCronExpr());
  }

}
