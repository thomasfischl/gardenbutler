package com.github.thomasfischl.gardenbutler.domain;

public class ActorAction implements DomainObject {

  private String actorName;

  private String action;

  private String param;

  private long executionTime;

  private String description;

  private Long id;

  public ActorAction(String actorName, String action, String param, String description) {
    super();
    this.actorName = actorName;
    this.action = action;
    this.param = param;
    this.description = description;
  }

  public void setExecutionTime(long executionTime) {
    this.executionTime = executionTime;
  }

  public String getDescription() {
    return description;
  }

  public long getExecutionTime() {
    return executionTime;
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

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

}
