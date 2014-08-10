package com.github.thomasfischl.gardenbutler.domain;

import java.util.Collection;
import java.util.Collections;

import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

public class DomainStore<T extends DomainObject> {

  private HTreeMap<Long, T> store;
  private Atomic.Long index;

  public DomainStore(DB db, String storeName, Serializer<?> serializer) {
    store = db.createHashMap(storeName).valueSerializer(serializer).counterEnable().makeOrGet();
    index = db.getAtomicLong(storeName + "-id");
  }

  public T store(T obj) {
    if (obj == null) {
      return obj;
    }

    if (obj.getId() == null) {
      obj.setId(index.incrementAndGet());
    }
    store.put(obj.getId(), obj);

    return obj;
  }

  public T load(long id) {
    return store.get(id);
  }

  public T remove(long id) {
    return store.remove(id);
  }

  public Collection<T> loadAll() {
    return Collections.unmodifiableCollection(store.values());
  }

}
