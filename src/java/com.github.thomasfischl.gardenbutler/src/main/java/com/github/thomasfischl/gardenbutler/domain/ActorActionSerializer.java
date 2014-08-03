package com.github.thomasfischl.gardenbutler.domain;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.mapdb.Serializer;

public class ActorActionSerializer implements Serializer<ActorAction>, Serializable {

  private static final long serialVersionUID = 1L;

  @Override
  public void serialize(DataOutput out, ActorAction value) throws IOException {
    out.writeUTF(value.getActorName());
    out.writeUTF(value.getAction());
    out.writeUTF(value.getParam());
  }

  @Override
  public ActorAction deserialize(DataInput in, int available) throws IOException {
    return new ActorAction(in.readUTF(), in.readUTF(), in.readUTF());
  }

  @Override
  public int fixedSize() {
    return -1;
  }

}
