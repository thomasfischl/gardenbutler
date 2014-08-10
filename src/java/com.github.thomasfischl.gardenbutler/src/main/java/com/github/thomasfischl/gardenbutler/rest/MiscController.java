package com.github.thomasfischl.gardenbutler.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/misc")
public class MiscController {

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<ResourceSupport> getRoot() {
    ResourceSupport result = new ResourceSupport();
    result.add(linkTo(methodOn(MiscController.class).getTime()).withRel("time"));
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(value = "/time", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getTime() {
    return new ResponseEntity<>(new Date().toString(), HttpStatus.OK);
  }

}
