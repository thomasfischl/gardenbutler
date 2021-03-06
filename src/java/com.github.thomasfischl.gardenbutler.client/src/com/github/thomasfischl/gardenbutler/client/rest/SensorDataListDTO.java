package com.github.thomasfischl.gardenbutler.client.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SensorDataListDTO {

  private List<SensorDataDTO> data = new ArrayList<SensorDataDTO>();

  public void addData(SensorDataDTO val) {
    data.add(val);
  }

  public void setData(List<SensorDataDTO> data) {
    this.data = data;
  }

  public List<SensorDataDTO> getData() {
    if (data == null) {
      return Collections.emptyList();
    }
    return data;
  }

}
