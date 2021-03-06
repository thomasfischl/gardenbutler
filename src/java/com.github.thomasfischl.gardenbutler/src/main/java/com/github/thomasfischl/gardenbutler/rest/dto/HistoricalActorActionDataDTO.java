package com.github.thomasfischl.gardenbutler.rest.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

public class HistoricalActorActionDataDTO extends ResourceSupport {

  private List<ActorActionDTO> data = new ArrayList<>();

  public List<ActorActionDTO> getData() {
    return data;
  }

  public void add(ActorActionDTO action) {
    data.add(action);
  }

  public class ActorActionDTO {

    private String actorName;

    private String action;

    private String param;

    private long executionTime;

    private String description;

    private Long id;

    public ActorActionDTO(String actorName, String action, String param, long executionTime, String description, Long id) {
      this.actorName = actorName;
      this.action = action;
      this.param = param;
      this.executionTime = executionTime;
      this.description = description;
      this.id = id;
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

    public Long getId() {
      return id;
    }

  }

}
