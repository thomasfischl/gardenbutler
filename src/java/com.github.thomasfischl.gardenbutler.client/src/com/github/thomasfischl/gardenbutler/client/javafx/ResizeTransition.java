package com.github.thomasfischl.gardenbutler.client.javafx;

import javafx.animation.Transition;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ResizeTransition extends Transition {

  private Pane ctrl;
  private double toHeight;
  private double fromHeight;

  public ResizeTransition(Pane ctrl, double toHeight) {
    this.ctrl = ctrl;

    this.fromHeight = ctrl.getHeight();
    this.toHeight = toHeight;

    setCycleDuration(Duration.millis(500));
    setRate(1);
    setAutoReverse(false);
    setCycleCount(1);
  }

  @Override
  protected void interpolate(double frac) {
    double heightDiff = (toHeight - fromHeight) * frac;
    double height = fromHeight + heightDiff;
    ctrl.setMaxHeight(height);
    ctrl.setPrefHeight(height);
  }

}
