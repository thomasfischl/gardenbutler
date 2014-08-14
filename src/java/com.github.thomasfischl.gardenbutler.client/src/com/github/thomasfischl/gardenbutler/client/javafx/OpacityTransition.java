package com.github.thomasfischl.gardenbutler.client.javafx;

import javafx.animation.Transition;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class OpacityTransition extends Transition {

  private Pane ctrl;
  private double targetValue;
  private double sourceValue;

  public OpacityTransition(Pane ctrl, double opacity) {
    this(ctrl, opacity, 300);
  }

  public OpacityTransition(Pane ctrl, double opacity, double duration) {
    if (opacity < 0 || opacity > 1) {
      throw new IllegalArgumentException("Invalid opacity value. Value must be between 0 and 1.");
    }

    this.ctrl = ctrl;
    this.targetValue = opacity;

    sourceValue = ctrl.getOpacity();

    setCycleDuration(Duration.millis(duration));
    setRate(1);
    setAutoReverse(false);
    setCycleCount(1);
  }

  @Override
  protected void interpolate(double frac) {
    double diff = (targetValue - sourceValue) * frac;
    double value = sourceValue + diff;
    ctrl.setOpacity(value);
    if (value == 0) {
      ctrl.setVisible(false);
    } else {
      ctrl.setVisible(true);
    }
  }

}
