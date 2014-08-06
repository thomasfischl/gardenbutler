package com.github.thomasfischl.gardenbutler.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.thomasfischl.gardenbutler.client.controls.TemperatureControl;
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
  private void init(){
    service.setController(this);
  }

  public void update(String name, double value) {
    if (!controls.containsKey(name)) {
      TemperatureControl ctrl = new TemperatureControl();
      boxMain.getChildren().add(ctrl);
      controls.put(name, ctrl);
    }

    TemperatureControl ctrl = controls.get(name);
    ctrl.setTemperature(value);
  }
}
