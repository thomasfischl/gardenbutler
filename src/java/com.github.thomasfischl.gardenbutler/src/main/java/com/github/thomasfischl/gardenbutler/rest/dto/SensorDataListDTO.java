package com.github.thomasfischl.gardenbutler.rest.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

public class SensorDataListDTO extends ResourceSupport {

  private List<SensorDataDTO> data = new ArrayList<SensorDataDTO>();

  public void addData(SensorDataDTO val) {
    data.add(val);
  }

  public void setData(List<SensorDataDTO> data) {
    this.data = data;
  }

  public List<SensorDataDTO> getData() {
    return data;
  }

}
