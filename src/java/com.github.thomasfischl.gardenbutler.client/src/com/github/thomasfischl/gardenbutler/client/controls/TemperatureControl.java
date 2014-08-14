package com.github.thomasfischl.gardenbutler.client.controls;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

import com.github.thomasfischl.gardenbutler.client.DI;
import com.github.thomasfischl.gardenbutler.client.javafx.ControlExpander;
import com.github.thomasfischl.gardenbutler.client.javafx.CssUtil;
import com.github.thomasfischl.gardenbutler.client.util.RollingAverage;

public class TemperatureControl extends Pane {

  private static final int MAX_TEMPERATURE = 40;
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

  private RollingAverage temperatureAverage = new RollingAverage(10);

  private ControlExpander controlExpander;

  private String name;

  public TemperatureControl(String name) {
    this.name = name;
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TemperatureControl.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    lbName.setText(name);

    controlExpander = new ControlExpander(this, paneChart, 170);
    setOnMouseClicked(e -> controlExpander.toggle());

    controlExpander.setOnExpanded(e -> showChart());
  }

  public void setTemperature(double temperature) {
    temperatureAverage.add(temperature);
    lbDisplay.setText(String.format("%.1f", temperature));

    if (temperature > temperatureAverage.getAverage()) {
      pTrendUp.setOpacity(1);
      pTrendDown.setOpacity(0.40);
    } else {
      pTrendDown.setOpacity(1);
      pTrendUp.setOpacity(0.40);
    }

    double temp = temperature / MAX_TEMPERATURE;
    pbTemperature.setProgress(temp);
    pbTemperature.setStyle("-fx-accent: " + CssUtil.hsb(170 - (170 * temp), 1.0, 1.0));
  }

  private void showChart() {

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    List<Data<String, Double>> values = DI.client().getHistoricalSensorData(name).getHistoricalData().entrySet().stream().filter(obj -> obj.getValue() != 0)
        .map(obj -> {
          return new Data<String, Double>(sdf.format(obj.getKey()).toString(), obj.getValue());
        }).collect(Collectors.toList());

    chHistroy.getData().clear();
    chHistroy.setAnimated(true);
    chHistroy.getXAxis().invalidateRange(Collections.emptyList());
    Series<String, Double> e = new Series<String, Double>();
    e.getData().addAll(values);
    chHistroy.getData().add(e);
  }

}
