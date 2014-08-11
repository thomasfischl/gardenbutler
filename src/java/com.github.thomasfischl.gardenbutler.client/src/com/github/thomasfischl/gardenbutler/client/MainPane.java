package com.github.thomasfischl.gardenbutler.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.thomasfischl.gardenbutler.client.controls.TemperatureControl;
import com.github.thomasfischl.gardenbutler.client.rest.HistoricalSensorDataDTO;
import com.github.thomasfischl.gardenbutler.client.rest.SensorDataDTO;
import com.github.thomasfischl.gardenbutler.client.service.RestService;

@Component
public class MainPane extends AnchorPane {

  private Map<String, TemperatureControl> controls = new HashMap<String, TemperatureControl>();

  @Autowired
  private RestService service;

  @FXML
  private VBox boxMain;

  @FXML
  private void onClose(ActionEvent e) {
    Platform.exit();
    System.exit(0);
  }

  public MainPane() {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPane.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

  }

  @PostConstruct
  private void init() {
    service.setController(this);
  }

  public void update(SensorDataDTO data) {
    if (!controls.containsKey(data.getName())) {
      TemperatureControl ctrl = new TemperatureControl(data.getName());

      ctrl.setOnChartShow(new EventHandler<Event>() {
        @Override
        public void handle(Event event) {
          HistoricalSensorDataDTO historicalData = service.getHistoricalSensorData(data);
          List<Data<String, Double>> values = new ArrayList<Data<String, Double>>();

          for (Entry<Long, Double> entry : historicalData.getHistoricalData().entrySet()) {
            if(entry.getValue() == 0){
              continue;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Data<String, Double> chartData = new Data<String, Double>(sdf.format(entry.getKey()).toString(), entry.getValue());
            values.add(chartData);
          }

          ctrl.setChartData(values);
        }
      });

      boxMain.getChildren().add(ctrl);
      controls.put(data.getName(), ctrl);
    }

    TemperatureControl ctrl = controls.get(data.getName());
    ctrl.setTemperature(data.getValue());
  }
}
