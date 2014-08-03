package com.github.thomasfischl.gardenbutler.domain;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.mapdb.Serializer;

public class ActorAction {

  private String actorName;

  private String action;

  private String param1;

  public ActorAction(String actorName, String action, String param1) {
    super();
    this.actorName = actorName;
    this.action = action;
    this.param1 = param1;
  }

  public static Serializer<ActorAction> getSerializer() {
    return new Serializer<ActorAction>() {
      @Override
      public void serialize(DataOutput out, ActorAction value) throws IOException {
        out.writeUTF(value.actorName);
        out.writeUTF(value.action);
        out.writeUTF(value.param1);
      }

      @Override
      public ActorAction deserialize(DataInput in, int available) throws IOException {
        return new ActorAction(in.readUTF(), in.readUTF(), in.readUTF());
      }

      @Override
      public int fixedSize() {
        return -1;
      }
    };
  }

}
