package com.github.thomasfischl.gardenbutler.client.javafx;

public class Util {

  public static void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
    }
  }

}
