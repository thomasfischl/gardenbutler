package com.github.thomasfischl.gardenbutler.client.controls;

import java.io.IOException;
import java.util.List;

import javafx.animation.SequentialTransition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

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
  @FXML
  private AnchorPane paneChart;
  @FXML
  private LineChart<String, Double> chHistroy;

  private double lastTemperature;

  private double temperature;

  private double originalHeight;

  private boolean expanded = false;

  private EventHandler<? super Event> onChartShowHandler;

  public TemperatureControl(String name) {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TemperatureControl.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    originalHeight = getMaxHeight();

    paneChart.setVisible(false);
    paneChart.setOpacity(0);

    setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        onPanelClick();
      }
    });
    
    lbName.setText(name);
  }

  private void onPanelClick() {

    SequentialTransition seqTrans = new SequentialTransition();
    if (expanded) {
      ResizeTransition transResize = new ResizeTransition(this, originalHeight);
      OpacityTransition transOpacity = new OpacityTransition(paneChart, 0);
      seqTrans.getChildren().add(transOpacity);
      seqTrans.getChildren().add(transResize);
    } else {
      ResizeTransition transResize = new ResizeTransition(this, originalHeight + 170);
      OpacityTransition transOpacity = new OpacityTransition(paneChart, 1);
      seqTrans.getChildren().add(transResize);
      seqTrans.getChildren().add(transOpacity);

      onChartShowHandler.handle(new Event(this, this, Event.ANY));
    }
    seqTrans.play();

    expanded = !expanded;
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

  public final void setOnChartShow(EventHandler<? super Event> value) {
    onChartShowHandler = value;
  }

  public void setChartData(List<Data<String, Double>> values) {
    chHistroy.getData().clear();
    Series<String, Double> e = new Series<String, Double>();
    e.getData().addAll(values);
    chHistroy.getData().add(e);
  }

}
