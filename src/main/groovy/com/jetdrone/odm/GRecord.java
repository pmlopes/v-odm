package com.jetdrone.odm;

import groovy.lang.Closure;
import org.vertx.java.core.Handler;

import java.util.Map;

public abstract class GRecord<PK> extends Record<PK> {

    public GRecord(Mapper mapper) {
        super(mapper);
    }

    public GRecord(Mapper mapper, Map<String, Object> data) {
        super(mapper, data);
    }

    public void putAt(String key, Object value) {
        super.putValue(key, value);
    }

    public Object getAt(String key) {
        return super.getValue(key);
    }

    public void save(final Closure<Boolean> handler) {
        super.save(new Handler<Boolean>() {
            @Override
            public void handle(Boolean event) {
                handler.call(event);
            }
        });
    }

    public void update(final Closure<Boolean> handler) {
        super.update(new Handler<Boolean>() {
            @Override
            public void handle(Boolean event) {
                handler.call(event);
            }
        });
    }

    public void remove(final Closure<Boolean> handler) {
        super.remove(new Handler<Boolean>() {
            @Override
            public void handle(Boolean event) {
                handler.call(event);
            }
        });
    }
}
