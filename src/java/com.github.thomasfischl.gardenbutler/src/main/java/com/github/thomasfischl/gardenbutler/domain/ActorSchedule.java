package com.github.thomasfischl.gardenbutler.domain;

public class ActorSchedule implements DomainObject {

  private Long id;

  private String actorName;

  private String action;

  private String param;

  private String cronExpr;

  public ActorSchedule(String actorName, String action, String param, String cronExpr) {
    super();
    this.actorName = actorName;
    this.action = action;
    this.param = param;
    this.cronExpr = cronExpr;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public Long getId() {
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

  @Override
  public int hashCode() {
    if (id == null) {
      return 0;
    }
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ActorSchedule) {
      if (id == null) {
        return false;
      }
      return id.equals(((ActorSchedule) obj).id);
    }
    return false;
  }

  @Override
  public String toString() {
    return id + ": " + actorName + ", " + action + ", " + param + ", " + cronExpr;
  }

}
