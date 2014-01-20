package com.jetdrone.odm.backend;

import com.jetdrone.odm.Mapper;
import com.jetdrone.odm.Record;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.eventbus.EventBus;

import java.util.List;

public abstract class MySQLMapper<R extends Record<String>> extends Mapper<String, R, String> {

    private static final String ALL = "*";

    public MySQLMapper(EventBus eventBus, String address, String entity) {
        super(eventBus, address, entity, "id");
    }

    public MySQLMapper(EventBus eventBus, String address, String entity, String id) {
        super(eventBus, address, entity, id);
    }

    @Override
    public void find(final String query, final AsyncResultHandler<List<R>> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void findOne(String query, AsyncResultHandler<R> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void findAll(AsyncResultHandler<List<R>> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(String query, R record, AsyncResultHandler<Void> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(String query, AsyncResultHandler<Void> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(R record, AsyncResultHandler<String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void findById(String id, AsyncResultHandler<R> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(R record, AsyncResultHandler<Void> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(R record, AsyncResultHandler<Void> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void count(String query, AsyncResultHandler<Number> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void count(AsyncResultHandler<Number> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void truncate(AsyncResultHandler<Void> callback) {
        throw new UnsupportedOperationException();
    }
}
