package com.github.thomasfischl.gardenbutler.microcontroller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

@Component
public class PiMicorcontroller {

  private GpioController gpio;
  private GpioPinDigitalOutput relay1;
  private GpioPinDigitalOutput relay2;

  private boolean initialized;

  private Map<String, Double> sensorValues = new HashMap<>();

  @PostConstruct
  public void init() {
    if (!initialized) {
      try {
        gpio = GpioFactory.getInstance();

        relay1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "Pump1", PinState.LOW);
        relay2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Pump2", PinState.LOW);

        initialized = true;

        test();
      } catch (UnsatisfiedLinkError e) {
        e.printStackTrace();
        // TODO improve error handling
      }
    }
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void test() {
  }

  public void activatePump1(long duration) {
    activatePump(relay1, duration);
  }

  public void activatePump2(long duration) {
    activatePump(relay2, duration);
  }

  private void activatePump(GpioPinDigitalOutput gpio, long duration) {
    gpio.high();
    silentSleep(duration);
    gpio.low();
  }

  private void silentSleep(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void readData() {
    readTemperatureSensors();
  }

  public Double getSensorValue(String sensorName) {
    if (!sensorValues.containsKey(sensorName)) {
      System.out.println("Sensor with name '" + sensorName + "' not found.");
      return null;
    }

    return sensorValues.get(sensorName);
  }

  private void readTemperatureSensors() {

    File w1DeviceDir = new File("/sys/bus/w1/devices");

    if (w1DeviceDir.exists()) {
      File[] files = w1DeviceDir.listFiles();
      if (files == null) {
        return;
      }

      for (File f : files) {
        if (f.isDirectory() && f.getName().startsWith("10-")) {
          File slaveFile = new File(f, "w1_slave");
          if (slaveFile.exists() && slaveFile.isFile()) {
            Double value = readTemperatureSensor(slaveFile);
            if (value != null) {
              System.out.println("Temperature: " + f.getName() + " => " + value);
              sensorValues.put("Temperature-" + f.getName(), value);
            }
          }
        }

      }
    }

    // file: /sys/bus/w1/devices/10-00080278f1b7/w1_slave

  }

  private Double readTemperatureSensor(File slaveFile) {
    // 37 00 4b 46 ff ff 07 10 1e t=27312
    try {
      List<String> lines = Files.readLines(slaveFile, Charsets.UTF_8);
      if (lines == null) {
        return null;
      }

      for (String line : lines) {
        if (line.contains("t=")) {
          return Double.valueOf(line.substring(line.indexOf("t=") + 2)) / 1000;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      // TODO improve error handling
    }
    return null;
  }

}
