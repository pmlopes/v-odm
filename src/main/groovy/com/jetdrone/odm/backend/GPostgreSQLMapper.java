package com.jetdrone.odm.backend;

import com.jetdrone.odm.Record;
import groovy.lang.Closure;
import org.vertx.groovy.core.AsyncResult;
import org.vertx.groovy.core.eventbus.EventBus;

import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.Map;

public abstract class GPostgreSQLMapper<R extends Record<Number>> extends PostgreSQLMapper<R> {

    public GPostgreSQLMapper(EventBus eventBus, String address, String entity) {
        super(eventBus.javaEventBus(), address, entity);
    }

    protected abstract R newRecord(final Map<String, Object> json);

    protected R newRecord(final JsonObject json) {
        return newRecord(json.toMap());
    }

    // Generic find

    public void find(final String query, final Closure<AsyncResult<List<R>>> callback) {
        super.find(query, new AsyncResultHandler<List<R>>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<List<R>> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    public void findOne(final String query, final Closure<AsyncResult<R>> callback) {
        super.findOne(query, new AsyncResultHandler<R>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<R> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    public void findAll(final Closure<AsyncResult<List<R>>> callback) {
        super.findAll(new AsyncResultHandler<List<R>>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<List<R>> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    // Generic update

    public void update(final String query, final R record, final Closure<AsyncResult<Void>> callback) {
        super.update(query, record, new AsyncResultHandler<Void>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<Void> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    // Generic remove

    public void remove(final String query, final Closure<AsyncResult<Void>> callback) {
        super.remove(query, new AsyncResultHandler<Void>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<Void> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    // Generic CRUD

    public void save(final R record, final Closure<AsyncResult<Number>> callback) {
        super.save(record, new AsyncResultHandler<Number>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<Number> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    public void findById(Number id, final Closure<AsyncResult<R>> callback) {
        super.findById(id, new AsyncResultHandler<R>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<R> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    public void update(final R record, final Closure<AsyncResult<Void>> callback) {
        super.update(record, new AsyncResultHandler<Void>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<Void> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    public void remove(final R record, final Closure<AsyncResult<Void>> callback) {
        super.remove(record, new AsyncResultHandler<Void>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<Void> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    // Generic utils

    public void count(final String query, final Closure<AsyncResult<Number>> callback) {
        super.count(query, new AsyncResultHandler<Number>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<Number> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    public void count(final Closure<AsyncResult<Number>> callback) {
        super.count(new AsyncResultHandler<Number>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<Number> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }

    public void truncate(final Closure<AsyncResult<Void>> callback) {
        super.truncate(new AsyncResultHandler<Void>() {
            @Override
            public void handle(org.vertx.java.core.AsyncResult<Void> asyncResult) {
                callback.call(new AsyncResult<>(asyncResult));
            }
        });
    }
}
