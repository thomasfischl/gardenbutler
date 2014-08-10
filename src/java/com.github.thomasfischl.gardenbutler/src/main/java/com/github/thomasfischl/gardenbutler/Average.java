package com.github.thomasfischl.gardenbutler;

public class Average {

  public double sum = 0;

  public long count = 0;

  public void add(double value) {
    count++;
    sum += value;
  }

  public double getAvg() {
    if (count == 0) {
      return 0;
    }
    return sum / count;
  }

}
