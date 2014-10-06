package com.github.thomasfischl.gardenbutler.client.controls;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import com.github.thomasfischl.gardenbutler.client.DI;
import com.github.thomasfischl.gardenbutler.client.javafx.ControlExpander;
import com.github.thomasfischl.gardenbutler.client.rest.HistoricalActorActionDataDTO.ActorActionDTO;

public class ActorControl extends Pane {

  @FXML
  private ListView<String> lvHistory;
  @FXML
  private AnchorPane paneHistroy;
  @FXML
  private Label lbName;
  @FXML
  private Label lbLoadAll;
  @FXML
  private Label lbTotalTime;

  private ControlExpander controlExpander;

  private String name;

  private boolean showAll;

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
    controlExpander.setOnExpanded(e -> updateHistory());

    setOnMouseClicked(e -> controlExpander.toggle());
    lbLoadAll.setOnMouseClicked(e -> {
      showAll = true;
      e.consume();
      controlExpander.toggle(false);
    });
  }

  private void updateHistory() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyy.MM.dd HH:mm");

    lvHistory.getItems().clear();
    List<ActorActionDTO> data = DI.client().getHistroyForPump(name).getData();

    long cumulativeTimeToday = data.stream()
        .filter(obj -> Instant.ofEpochMilli(obj.getExecutionTime()).atZone(ZoneId.systemDefault()).toLocalDate().equals(LocalDate.now()))
        .mapToLong(obj -> Long.valueOf(obj.getParam())).sum();

    lbTotalTime.setText(String.format("Cumulative Time Today: %.2f sec ", (double) cumulativeTimeToday / 1000));

    Stream<ActorActionDTO> stream = data.stream().sorted((a, b) -> a.getExecutionTime() > b.getExecutionTime() ? -1 : 1);
    if (!showAll) {
      stream = stream.limit(10);
    }
    stream.forEach(obj -> {
      String row = sdf.format(obj.getExecutionTime()) + " - ";
      if (obj.getDescription().contains("Time:")) {
        row += obj.getDescription().substring(0, obj.getDescription().indexOf("Time:"));
      } else {
        row += obj.getDescription();
      }
      row += String.format(" (%.2f sec)", (double) Long.valueOf(obj.getParam()) / 1000);

      lvHistory.getItems().add(row);
    });

    double height = lvHistory.getItems().size() * lvHistory.getFixedCellSize() + 80;

    lbLoadAll.setVisible(!showAll);

    paneHistroy.setMaxHeight(height);
    paneHistroy.setPrefHeight(height);
    controlExpander.setExpandedHeight(height + 20);

    showAll = false;
  }

  @FXML
  private void onActivate(ActionEvent e) {
    DI.client().activatePump(name);
  }

}