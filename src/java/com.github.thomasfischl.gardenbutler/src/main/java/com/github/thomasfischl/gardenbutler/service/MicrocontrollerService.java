package com.github.thomasfischl.gardenbutler.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;

import com.github.thomasfischl.gardenbutler.Configuration;
import com.github.thomasfischl.gardenbutler.SensorMetaData;
import com.github.thomasfischl.gardenbutler.domain.ActorAction;
import com.github.thomasfischl.gardenbutler.domain.ActorSchedule;
import com.github.thomasfischl.gardenbutler.domain.DomainStore;
import com.github.thomasfischl.gardenbutler.microcontroller.IMicrocontrollerBridge;

@Service
public class MicrocontrollerService {

  private static final Logger LOG = LoggerFactory.getLogger(MicrocontrollerService.class);
  
  @Autowired
  private StoreService storeService;

  @Autowired
  private Configuration config;

  @Autowired
  private IMicrocontrollerBridge microcontroller;

  private Map<Long, Long> lastScheduleRuns = new HashMap<>();

  // every 30 seconds
  @Scheduled(fixedRate = 30000)
  public void readData() {
    long time = System.currentTimeMillis();
    microcontroller.readData();

    for (SensorMetaData sensor : config.getSensorNameList()) {
      Double value = microcontroller.getSensorValue(sensor.hardwareId);
      if (value != null) {
        storeService.storeSensorData(sensor.displayName, value, time);
      }
    }
  }

  // every 10 seconds
  @Scheduled(fixedRate = 10000)
  public void executeAction() {

    ActorAction action = storeService.nextActorAction();
    if (action == null) {
      return;
    }

    if ("activate".equalsIgnoreCase(action.getAction())) {
      long duration = Long.valueOf(action.getParam());
      if ("pump1".equalsIgnoreCase(action.getActorName())) {
        microcontroller.activatePump1(duration);
      } else if ("pump2".equalsIgnoreCase(action.getActorName())) {
        microcontroller.activatePump2(duration);
      } else {
        return;
      }
    } else {
      return;
    }

    action.setExecutionTime(System.currentTimeMillis());
    storeService.getActorActionStore().store(action);
  }

  // every 10 seconds
  @Scheduled(fixedRate = 10000)
  public void checkActorSchedule() {
    DomainStore<ActorSchedule> store = storeService.getActorSchedulesStore();

    for (ActorSchedule schedule : store.loadAll()) {
      CronSequenceGenerator generator = new CronSequenceGenerator(schedule.getCronExpr());
      Date date = generator.next(new Date());

      if (date.getTime() < System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10)
          && !Long.valueOf(date.getTime()).equals(lastScheduleRuns.get(schedule.getId()))) {
        executeActorSchedule(schedule, date);
      }
    }

  }

  private void executeActorSchedule(ActorSchedule schedule, Date date) {
    LOG.info("Execute schedule: " + schedule + " (" + date.toString() + ")");
    lastScheduleRuns.put(schedule.getId(), date.getTime());
    storeService.queueActorAction(new ActorAction(schedule.getActorName(), schedule.getAction(), schedule.getParam(), "Triggered by schedule '"
        + schedule.getId() + "'. Time: " + date.getTime()));
  }
}
