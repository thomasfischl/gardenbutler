package com.github.thomasfischl.gardenbutler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.thomasfischl.gardenbutler.domain.ActorAction;
import com.github.thomasfischl.gardenbutler.domain.SensorData;
import com.google.common.collect.Maps;

@Service
public class StoreService {

  @Value("${database.file}")
  private String databaseFilePath;

  private DB db;

  private HTreeMap<String, Double> currSensorData;

  private BlockingQueue<ActorAction> actorActionQueue;

  @PostConstruct
  public void init() {

    if (databaseFilePath == null) {
      databaseFilePath = "gardenbutler.db";
    }
    File dbFile = new File(databaseFilePath);
    dbFile.getParentFile().mkdirs();

    db = DBMaker.newFileDB(dbFile).closeOnJvmShutdown().make();
    currSensorData = db.createHashMap("curr-sensor-data").counterEnable().makeOrGet();
    actorActionQueue = db.createQueue("actor-actions", ActorAction.getSerializer(), true);
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
    return actorActionQueue.poll();
  }

  private String getMapName(String sensorName) {
    return "history-data-" + sensorName;
  }

}
