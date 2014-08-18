package com.github.thomasfischl.gardenbutler.client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginPage extends AnchorPane {

  @FXML
  private PasswordField tfPassword;
  @FXML
  private ComboBox<String> cbServers;

  @FXML
  private void doClose(ActionEvent e) {
    Platform.exit();
    System.exit(0);
  }

  @FXML
  private void doLogin(ActionEvent event) {
    try {
      DI.client().init(cbServers.getSelectionModel().getSelectedItem());
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    getScene().getWindow().hide();

    Stage stage = new Stage();
    stage.setTitle("Gardenbutler v2");
    stage.setScene(new Scene(new MainPane(), 490, 540));
    stage.show();
  }

  public LoginPage() {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    cbServers.getItems().add("http://localhost:8000");
    cbServers.getItems().add("http://90.146.153.210:8000");
    cbServers.getSelectionModel().select(1);
  }
}
