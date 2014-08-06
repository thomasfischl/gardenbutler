package com.github.thomasfischl.gardenbutler.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GradenbutlerApplication extends Application {

  private AnnotationConfigApplicationContext ctx;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage root) throws Exception {
    ctx = new AnnotationConfigApplicationContext(SpringJavaConfig.class);
    Font.loadFont(GradenbutlerApplication.class.getResource("/digitalfont.ttf").toExternalForm(), 10);
    root.setTitle("Gardenbutler v2");
    root.setScene(new Scene(ctx.getBean(LoginPage.class), 400, 160));
    root.show();
  }

  @Override
  public void stop() throws Exception {
    if (ctx != null) {
      ctx.stop();
    }
    System.exit(0);
  }

}
