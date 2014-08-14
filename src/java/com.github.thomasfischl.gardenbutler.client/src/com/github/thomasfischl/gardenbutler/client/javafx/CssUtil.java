package com.github.thomasfischl.gardenbutler.client.javafx;

public class CssUtil {

  public static String hsb(double hue, double saturation, double brightness) {
    return String.format("hsb(%d, %d%% , %d%%)", (int) hue, (int) saturation * 100, (int) brightness * 100);
  }
  
}
