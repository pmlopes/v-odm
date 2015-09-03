package com.jetdrone.odm;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

public abstract class Mapper<PK, R extends Record<PK>, Q> {

    protected final String entity;

    public final String ID;

    public Mapper(String entity, String id) {
        this.entity = entity;
        this.ID = id;
    }

    protected abstract R newRecord(final JsonObject json);

    protected R newRecord() {
        return newRecord(new JsonObject());
    }

    // Generic find

    public abstract void find(final Q query, final Handler<AsyncResult<List<R>>> callback);

    public abstract void findOne(final Q query, final Handler<AsyncResult<R>> callback);

    public abstract void findAll(final Handler<AsyncResult<List<R>>> callback);

    // Generic update

    public abstract void update(final Q query, final R record, final Handler<AsyncResult<Void>> callback);

    // Generic remove

    public abstract void remove(final Q query, final Handler<AsyncResult<Void>> callback);

    // Generic CRUD

    public abstract void save(final R record, final Handler<AsyncResult<PK>> callback);

    public abstract void findById(PK id, final Handler<AsyncResult<R>> callback);

    public abstract void update(final R record, final Handler<AsyncResult<Void>> callback);

    public abstract void remove(final R record, final Handler<AsyncResult<Void>> callback);

    // Generic utils

    public abstract void count(final Q query, final Handler<AsyncResult<Long>> callback);

    public abstract void count(final Handler<AsyncResult<Long>> callback);

    public abstract void truncate(final Handler<AsyncResult<Void>> callback);
}
