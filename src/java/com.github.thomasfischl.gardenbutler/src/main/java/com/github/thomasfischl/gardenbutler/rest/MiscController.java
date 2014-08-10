package com.github.thomasfischl.gardenbutler.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.thomasfischl.gardenbutler.Configuration;

@Controller
@RequestMapping("/rest/misc")
public class MiscController {

  @Autowired
  private Configuration config;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<ResourceSupport> getRoot() {
    ResourceSupport result = new ResourceSupport();
    result.add(linkTo(methodOn(MiscController.class).getTime()).withRel("time"));
    result.add(linkTo(methodOn(MiscController.class).getLog()).withRel("log"));
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @RequestMapping(value = "/time", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getTime() {
    return new ResponseEntity<>(new Date().toString(), HttpStatus.OK);
  }

  @RequestMapping(value = "/log", method = RequestMethod.GET)
  @ResponseBody
  public FileSystemResource getLog() {
    return new FileSystemResource(new File( config.getLoggingPath() + "spring.log"));
  }

}
