package com.jetdrone.odm;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

import java.util.Collections;
import java.util.List;

public abstract class Mapper<PK, R extends Record<PK>, Q> {

    protected <T> AsyncResult<T> wrapResult(final Throwable error, final T result) {
        return new AsyncResult<T>() {
            @Override
            public T result() {
                return result;
            }

            @Override
            public Throwable cause() {
                return error;
            }

            @Override
            public boolean succeeded() {
                return error == null;
            }

            @Override
            public boolean failed() {
                return error != null;
            }
        };
    }

    protected final String entity;
    protected final EventBus eventBus;
    protected final String address;

    public final String ID;

    public Mapper(EventBus eventBus, String address, String entity, String id) {
        this.eventBus = eventBus;
        this.address = address;
        this.entity = entity;
        this.ID = id;
    }

    protected abstract R newRecord(final JsonObject json);

    protected R newRecord() {
        return newRecord(new JsonObject());
    }

    // Generic find

    public abstract void find(final Q query, final AsyncResultHandler<List<R>> callback);

    public abstract void findOne(final Q query, final AsyncResultHandler<R> callback);

    public abstract void findAll(final AsyncResultHandler<List<R>> callback);

    // Generic update

    public abstract void update(final Q query, final R record, final AsyncResultHandler<Void> callback);

    // Generic remove

    public abstract void remove(final Q query, final AsyncResultHandler<Void> callback);

    // Generic CRUD

    public abstract void save(final R record, final AsyncResultHandler<PK> callback);

    public abstract void findById(PK id, final AsyncResultHandler<R> callback);

    public abstract void update(final R record, final AsyncResultHandler<Void> callback);

    public abstract void remove(final R record, final AsyncResultHandler<Void> callback);

    // Generic utils

    public abstract void count(final Q query, final AsyncResultHandler<Number> callback);

    public abstract void count(final AsyncResultHandler<Number> callback);

    public abstract void truncate(final AsyncResultHandler<Void> callback);
}
