package com.github.thomasfischl.gardenbutler.client;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = "com.github.thomasfischl")
@EnableAutoConfiguration
@EnableScheduling
public class SpringJavaConfig {

}
