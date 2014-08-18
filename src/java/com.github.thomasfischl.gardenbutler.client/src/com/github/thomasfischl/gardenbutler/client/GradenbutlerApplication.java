package com.github.thomasfischl.gardenbutler.client;

import java.io.FileInputStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GradenbutlerApplication extends Application {

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage root) throws Exception {
    try {
      Font.loadFont(GradenbutlerApplication.class.getResource("/digitalfont.ttf").toExternalForm(), 10);
    } catch (Exception e) {
      Font.loadFont(new FileInputStream("./src/digitalfont.ttf"), 10);
    }
    root.setTitle("Gardenbutler v2");
    root.setScene(new Scene(new LoginPage(), 400, 160));
    root.show();
  }

  @Override
  public void stop() throws Exception {
    System.exit(0);
  }

}
