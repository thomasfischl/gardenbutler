package com.github.thomasfischl.gardenbutler.client.controls;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

public class TemperatureControl extends Pane {

  @FXML
  private Label lbDisplay;
  @FXML
  private Label lbName;
  @FXML
  private Polygon pTrendDown;
  @FXML
  private ProgressBar pbTemperature;
  @FXML
  private Polygon pTrendUp;

  private double lastTemperature;

  private double temperature;

  private boolean expand = true;

  public TemperatureControl() {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TemperatureControl.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        TemperatureControl self = TemperatureControl.this;

        Timeline timeline = new Timeline();
        timeline.setAutoReverse(false);
        timeline.setCycleCount(100);

        KeyFrame frame = new KeyFrame(Duration.millis(25), new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            double height;
            if (expand) {
              height = self.getHeight() - 10;
            } else {
              height = self.getHeight() + 10;
            }
            self.setMaxHeight(height);
            self.setPrefHeight(height);
          }
        });
        timeline.getKeyFrames().add(frame);
        timeline.play();

        expand = !expand;
      }
    });
  }

  public void setTemperature(double temperature) {
    this.lastTemperature = this.temperature;
    this.temperature = temperature;

    lbDisplay.setText(String.format("%.1f", temperature));

    if (temperature > lastTemperature) {
      pTrendUp.setOpacity(1);
      pTrendDown.setOpacity(0.40);
    } else {
      pTrendDown.setOpacity(1);
      pTrendUp.setOpacity(0.40);
    }

    pbTemperature.setProgress(temperature / 40);
  }

}
