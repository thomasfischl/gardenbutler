package com.github.thomasfischl.gardenbutler.rest.dto;

import org.springframework.hateoas.ResourceSupport;

public class ScheduleDTO extends ResourceSupport {

  private String actorName;

  private String action;

  private String param;

  private String cronExpr;

  private long id;

  public ScheduleDTO(long id, String actorName, String action, String param, String cronExpr) {
    this.id = id;
    this.actorName = actorName;
    this.action = action;
    this.param = param;
    this.cronExpr = cronExpr;
  }

  public long getObjectId() {
    return id;
  }

  public String getActorName() {
    return actorName;
  }

  public String getAction() {
    return action;
  }

  public String getParam() {
    return param;
  }

  public String getCronExpr() {
    return cronExpr;
  }

}
