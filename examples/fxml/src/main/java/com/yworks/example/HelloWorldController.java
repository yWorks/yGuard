package com.yworks.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloWorldController {

  @FXML
  private Label clickCountLabel;

  private int clickCount;

  @FXML
  private void onClicked( ActionEvent event ) {
    ++clickCount;
    clickCountLabel.setText(String.format("You clicked %d time%s.", clickCount, clickCount == 1 ? "" : "s"));
  }
}
