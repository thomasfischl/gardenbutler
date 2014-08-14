package com.github.thomasfischl.gardenbutler.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.github.thomasfischl.gardenbutler.client.controls.ActorControl;
import com.github.thomasfischl.gardenbutler.client.controls.TemperatureControl;
import com.github.thomasfischl.gardenbutler.client.javafx.OpacityTransition;
import com.github.thomasfischl.gardenbutler.client.javafx.Util;
import com.github.thomasfischl.gardenbutler.client.rest.SensorDataDTO;
import com.github.thomasfischl.gardenbutler.client.service.RestService;

@Component
@Lazy
public class MainPane extends AnchorPane {

  private Map<String, TemperatureControl> controls = new HashMap<String, TemperatureControl>();

  @Autowired
  private RestService service;

  @FXML
  private BorderPane paneLoading;

  @FXML
  private ProgressIndicator piLoadingIndicator;

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

    piLoadingIndicator.setProgress(-1);
  }

  @PostConstruct
  private void init() {
    DI.getTaskExecutor().execute(() -> loadData());
  }

  private void loadData() {
    Util.sleep(2000);

    service.getSensorNames().forEach(name -> Platform.runLater(() -> addSensor(name)));
    Platform.runLater(() -> addPump("pump1", "Pump 1"));
    Platform.runLater(() -> addPump("pump2", "Pump 2"));

    service.setController(this);

    Util.sleep(1000);
    Platform.runLater(() -> paneLoading.setVisible(false));
  }

  private void addPump(String name, String displayName) {
    ActorControl pump = new ActorControl(name, displayName);
    pump.setOpacity(0);

    boxMain.getChildren().add(pump);
    new OpacityTransition(pump, 1, 1000).play();
  }

  private void addSensor(String name) {
    if (name.equals("Temp 1")) {
      return;
    }
    if (!controls.containsKey(name)) {
      TemperatureControl ctrl = new TemperatureControl(name);
      ctrl.setOpacity(0);

      boxMain.getChildren().add(ctrl);
      controls.put(name, ctrl);
      new OpacityTransition(ctrl, 1, 1000).play();
    }
  }

  public void update(SensorDataDTO data) {
    TemperatureControl ctrl = controls.get(data.getName());
    if (ctrl != null) {
      ctrl.setTemperature(data.getValue());
    }
  }
}
