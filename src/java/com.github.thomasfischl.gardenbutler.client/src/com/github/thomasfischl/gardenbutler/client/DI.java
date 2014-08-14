package com.github.thomasfischl.gardenbutler.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.thomasfischl.gardenbutler.client.service.RestService;

public class DI {

  private static AnnotationConfigApplicationContext ctx;
  private static ExecutorService executorService;

  public static void setApplicationContext(AnnotationConfigApplicationContext ctx) {
    DI.ctx = ctx;
  }

  public static <T> T inject(Class<T> clazz) {
    return ctx.getBean(clazz);
  }

  public static RestService client() {
    return ctx.getBean(RestService.class);
  }

  public static ExecutorService getTaskExecutor() {
    if (executorService == null) {
      executorService = Executors.newCachedThreadPool();
    }
    return executorService;
  }

}
