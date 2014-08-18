package com.github.thomasfischl.gardenbutler.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.github.thomasfischl.gardenbutler.client.rest.IRestClient;
import com.github.thomasfischl.gardenbutler.client.rest.RetrofitRestClient;

public class DI {

  private static ScheduledExecutorService executorService;
  private static IRestClient client;

  public static IRestClient client() {
    if (client == null) {
      client = new RetrofitRestClient();
    }
    return client;
  }

  public static ScheduledExecutorService getExecutor() {
    if (executorService == null) {
      executorService = Executors.newScheduledThreadPool(2);
    }
    return executorService;
  }

}
