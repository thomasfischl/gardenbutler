package com.github.thomasfischl.gardenbutler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.thomasfischl.gardenbutler.Configuration;
import com.github.thomasfischl.gardenbutler.domain.ActorAction;
import com.github.thomasfischl.gardenbutler.domain.ActorSchedule;
import com.github.thomasfischl.gardenbutler.domain.DomainStore;
import com.github.thomasfischl.gardenbutler.domain.JsonSerializer;
import com.github.thomasfischl.gardenbutler.domain.SensorData;
import com.google.common.collect.Maps;

@Service
public class StoreService {

  @Autowired
  private Configuration config;

  private DB db;

  private HTreeMap<String, Double> currSensorData;

  private DomainStore<ActorSchedule> actorSchedulesStore;

  private BlockingQueue<ActorAction> actorActionQueue;

  private DomainStore<ActorAction> actorActionStore;

  @PostConstruct
  public void init() {

    String databaseFilePath = config.getDatabaseFilePath();
    if (databaseFilePath == null) {
      databaseFilePath = "gardenbutler.db";
    }
    File dbFile = new File(databaseFilePath);
    if (dbFile.getParentFile() != null) {
      dbFile.getParentFile().mkdirs();
    }

    db = DBMaker.newFileDB(dbFile).closeOnJvmShutdown().make();
    currSensorData = db.createHashMap("curr-sensor-data").counterEnable().makeOrGet();

    if (db.exists("actor-actions")) {
      actorActionQueue = db.getQueue("actor-actions");
    } else {
      actorActionQueue = db.createQueue("actor-actions", new JsonSerializer<ActorAction>(ActorAction.class), true);
    }

    actorSchedulesStore = new DomainStore<ActorSchedule>(db, "actor-schedule-store", new JsonSerializer<ActorSchedule>(ActorSchedule.class));
    actorActionStore = new DomainStore<ActorAction>(db, "actor-action-store", new JsonSerializer<ActorAction>(ActorAction.class));
  }

  public List<SensorData> loadCurrentSensorData() {
    List<SensorData> result = new ArrayList<SensorData>();
    for (Entry<String, Double> entry : currSensorData.entrySet()) {
      result.add(new SensorData(entry.getKey(), entry.getValue()));
    }
    return result;
  }

  public void storeSensorData(String sensorName, double value, long timestamp) {
    currSensorData.put(sensorName, value);

    BTreeMap<Long, Double> treeMap = db.getTreeMap(getMapName(sensorName));
    if (treeMap == null) {
      treeMap = db.createTreeMap(getMapName(sensorName)).makeLongMap();
    }

    treeMap.put(timestamp, value);
    db.commit();
  }

  public Map<Long, Double> loadHistoricalSensorValues(String sensorName) {
    BTreeMap<Long, Double> treeMap = db.getTreeMap(getMapName(sensorName));
    if (treeMap == null) {
      return Maps.newTreeMap();
    }
    return Maps.newTreeMap(treeMap);
  }

  public void queueActorAction(ActorAction action) {
    try {
      actorActionQueue.put(action);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public ActorAction nextActorAction() {
    try {
      return actorActionQueue.poll(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public DomainStore<ActorSchedule> getActorSchedulesStore() {
    return actorSchedulesStore;
  }

  public DomainStore<ActorAction> getActorActionStore() {
    return actorActionStore;
  }

  private String getMapName(String sensorName) {
    return "history-data-" + sensorName;
  }

}
