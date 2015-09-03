package com.jetdrone.odm;

import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.Map;

public abstract class Record<PK> extends JsonObject {

  private final Mapper mapper;

  public PK getId() {
    return (PK) getValue(mapper.ID);
  }

  public Record(Mapper mapper) {
    super();
    this.mapper = mapper;
  }

  public Record(Mapper mapper, Map<String, Object> data) {
    super(data);
    this.mapper = mapper;
  }

  public boolean isValid() {
    return true;
  }

  public void save(final Handler<Boolean> handler) {
    mapper.save(this, new AsyncResultHandler<PK>() {
      @Override
      public void handle(AsyncResult<PK> save) {
        if (save.failed()) {
          handler.handle(false);
          return;
        }
        // extract the id to the object properties
        put(mapper.ID, save.result());
        handler.handle(true);
      }
    });
  }

  public void update(final Handler<Boolean> handler) {
    mapper.update(this, new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> update) {
        if (update.failed()) {
          handler.handle(false);
          return;
        }
        handler.handle(true);
      }
    });
  }

  public void remove(final Handler<Boolean> handler) {
    mapper.remove(this, new Handler<AsyncResult<String>>() {
      @Override
      public void handle(AsyncResult<String> remove) {
        if (remove.failed()) {
          handler.handle(false);
          return;
        }
        handler.handle(true);
      }
    });
  }
}
