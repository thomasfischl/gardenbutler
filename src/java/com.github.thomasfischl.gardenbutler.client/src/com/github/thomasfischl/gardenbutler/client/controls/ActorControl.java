package com.github.thomasfischl.gardenbutler.client.controls;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import com.github.thomasfischl.gardenbutler.client.DI;
import com.github.thomasfischl.gardenbutler.client.javafx.ControlExpander;

public class ActorControl extends Pane {

  @FXML
  private ListView<String> lvHistory;
  @FXML
  private AnchorPane paneHistroy;
  @FXML
  private Label lbName;

  private ControlExpander controlExpander;

  private String name;

  public ActorControl(String name, String displayName) {
    this.name = name;
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ActorControl.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    lbName.setText(displayName);

    controlExpander = new ControlExpander(this, paneHistroy, 170);
    controlExpander.setOnExpanded(e -> updateHistroy());

    setOnMouseClicked(e -> controlExpander.toggle());
  }

  private void updateHistroy() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyy.MM.dd HH:mm");

    lvHistory.getItems().clear();
    DI.client().getHistroyForPump(name).getData().stream().sorted((a, b) -> a.getExecutionTime() > b.getExecutionTime() ? -1 : 1).forEach(obj -> {
      String row = sdf.format(obj.getExecutionTime()) + " - ";
      if (obj.getDescription().contains("Time:")) {
        row += obj.getDescription().substring(0, obj.getDescription().indexOf("Time:"));
      } else {
        row += obj.getDescription();
      }

      row += " (" + obj.getParam() + "ms )";

      lvHistory.getItems().add(row);
    });
  }

  @FXML
  private void onActivate(ActionEvent e) {
    DI.client().activatePump(name);
  }

}