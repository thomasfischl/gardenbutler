package com.github.thomasfischl.gardenbutler.domain;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.mapdb.Serializer;

import com.google.gson.Gson;

public class JsonSerializer<T> implements Serializer<T>, Serializable {

  private static final long serialVersionUID = 1L;
  private Class<T> clazz;

  public JsonSerializer(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public void serialize(DataOutput out, T value) throws IOException {
    Gson gson = new Gson();
    out.writeUTF(gson.toJson(value));
  }

  @Override
  public T deserialize(DataInput in, int available) throws IOException {
    Gson gson = new Gson();
    return gson.fromJson(in.readUTF(), clazz);
  }

  @Override
  public int fixedSize() {
    return -1;
  }

}
