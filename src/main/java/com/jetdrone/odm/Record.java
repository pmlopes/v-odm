package com.jetdrone.odm;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public abstract class Record<PK> extends JsonObject {

  private final Mapper<PK, Record<PK>, ?> mapper;

  public PK getId() {
    return (PK) getValue(mapper.ID);
  }

  public Record(Mapper mapper) {
    super();
    this.mapper = mapper;
  }

  public Record(Mapper mapper, JsonObject data) {
    super(data.getMap());
    this.mapper = mapper;
  }

  public void save(final Handler<Boolean> handler) {
    mapper.save(this, save -> {
      if (save.failed()) {
        handler.handle(false);
        return;
      }
      // extract the id to the object properties
      put(mapper.ID, save.result());
      handler.handle(true);
    });
  }

  public void update(final Handler<Boolean> handler) {
    mapper.update(this, update -> {
      if (update.failed()) {
        handler.handle(false);
        return;
      }
      handler.handle(true);
    });
  }

  public void remove(final Handler<Boolean> handler) {
    mapper.remove(this, remove -> {
      if (remove.failed()) {
        handler.handle(false);
        return;
      }
      handler.handle(true);
    });
  }
}
