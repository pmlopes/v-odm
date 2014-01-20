package com.jetdrone.odm;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

public abstract class Record<PK> extends JsonObject {

    private final Mapper mapper;

    public PK getId() {
        return getValue(mapper.ID);
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

    @SuppressWarnings("unchecked")
    public void save(final Handler<Boolean> handler) {
        mapper.save(this, new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> save) {
                if (save.failed()) {
                    handler.handle(false);
                    return;
                }
                // extract the id to the object properties
                putValue(mapper.ID, save.result());
                handler.handle(true);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void update(final Handler<Boolean> handler) {
        mapper.update(this, new AsyncResultHandler<Void>() {
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

    @SuppressWarnings("unchecked")
    public void remove(final Handler<Boolean> handler) {
        mapper.remove(this, new AsyncResultHandler<String>() {
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
