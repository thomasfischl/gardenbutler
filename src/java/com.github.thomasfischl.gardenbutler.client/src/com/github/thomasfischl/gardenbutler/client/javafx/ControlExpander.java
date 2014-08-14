package com.github.thomasfischl.gardenbutler.client.javafx;

import javafx.animation.SequentialTransition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;

public class ControlExpander {

  private double originalHeight;

  private double expandSize;

  private boolean expanded = false;

  private Pane root;

  private Pane expandedPane;

  private EventHandler<? super Event> onChartShowHandler;

  public ControlExpander(Pane root, Pane expandedPane, double expandSize) {
    this.root = root;
    this.expandedPane = expandedPane;
    this.expandSize = expandSize;

    originalHeight = root.getMaxHeight();

    expandedPane.setVisible(false);
    expandedPane.setOpacity(0);
  }

  public void toggle() {
    SequentialTransition seqTrans = new SequentialTransition();
    if (expanded) {
      ResizeTransition transResize = new ResizeTransition(root, originalHeight);
      OpacityTransition transOpacity = new OpacityTransition(expandedPane, 0);
      seqTrans.getChildren().add(transOpacity);
      seqTrans.getChildren().add(transResize);
    } else {
      ResizeTransition transResize = new ResizeTransition(root, originalHeight + expandSize);
      OpacityTransition transOpacity = new OpacityTransition(expandedPane, 1);
      seqTrans.getChildren().add(transResize);
      seqTrans.getChildren().add(transOpacity);

      if (onChartShowHandler != null) {
        onChartShowHandler.handle(new Event(root, root, Event.ANY));
      }
    }
    
    seqTrans.play();
    expanded = !expanded;
  }

  public final void setOnExpanded(EventHandler<? super Event> value) {
    onChartShowHandler = value;
  }

}
