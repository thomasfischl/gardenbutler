package com.github.thomasfischl.gardenbutler.domain;

import org.mapdb.Serializer;

public class ActorAction {

  private String actorName;

  private String action;

  private String param;

  public ActorAction(String actorName, String action, String param) {
    super();
    this.actorName = actorName;
    this.action = action;
    this.param = param;
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

  public static Serializer<ActorAction> getSerializer() {
    return new ActorActionSerializer();
  }

}
