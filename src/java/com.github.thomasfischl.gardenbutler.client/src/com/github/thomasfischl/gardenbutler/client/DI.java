package com.github.thomasfischl.gardenbutler.client;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DI {

  private static AnnotationConfigApplicationContext ctx;

  public static void setApplicationContext(AnnotationConfigApplicationContext ctx) {
    DI.ctx = ctx;
  }

  public static <T> T inject(Class<T> clazz) {
    return ctx.getBean(clazz);
  }

}
