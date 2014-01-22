package com.jetdrone.odm.backend;

import com.jetdrone.odm.Mapper;
import com.jetdrone.odm.Record;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.Set;

public abstract class RedisMapper<R extends Record<String>> extends Mapper<String, R, String> {

    private static final String ALL = "*";
    private static final String SEP = ":";

    public RedisMapper(EventBus eventBus, String address, String entity) {
        super(eventBus, address, entity, "id");
    }

    public RedisMapper(EventBus eventBus, String address, String entity, String id) {
        super(eventBus, address, entity, id);
    }

    private JsonArray keyValues(JsonArray json, R record) {
        Set<String> fields = record.getFieldNames();
        if (fields != null) {
            for (String f : fields) {
                json.add(f);
                Object o = record.getValue(f);
                if (o == null) {
                    json.add("null");
                } else {
                    if (o instanceof Boolean || o instanceof Number || o instanceof String) {
                        json.add(o);
                    } else {
                        throw new RuntimeException("Unsupported value type: " + o.getClass().getName());
                    }
                }
            }
        }
        return json;
    }


    @Override
    public void find(final String query, final AsyncResultHandler<List<R>> callback) {
        callback.handle(wrapResult(new UnsupportedOperationException(), (List<R>) null));
    }

    @Override
    public void findOne(String query, final AsyncResultHandler<R> callback) {
        JsonArray args = new JsonArray().add(entity + SEP + query);

        final JsonObject json = new JsonObject()
                .putString("command", "hgetall")
                .putArray("args", args);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (R) null));
                    return;
                }

                if ("ok".equals(status)) {
                    callback.handle(wrapResult(null, newRecord(new JsonObject(event.body().getString("value")))));
                }
            }
        });
    }

    @Override
    public void findAll(AsyncResultHandler<List<R>> callback) {
        find(ALL, callback);
    }

    @Override
    public void update(String query, R record, final AsyncResultHandler<Void> callback) {
        JsonArray args = new JsonArray().add(entity + SEP + query);

        try {
            keyValues(args, record);
        } catch (RuntimeException re) {
            callback.handle(wrapResult(re, (Void) null));
            return;
        }

        final JsonObject json = new JsonObject()
                .putString("command", "hmset")
                .putArray("args", args);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Void) null));
                    return;
                }

                if ("ok".equals(status)) {
                    callback.handle(wrapResult(null, (Void) null));
                }
            }
        });
    }

    @Override
    public void remove(String query, final AsyncResultHandler<Void> callback) {
        JsonArray args = new JsonArray().add(entity + SEP + query);

        final JsonObject json = new JsonObject()
                .putString("command", "del")
                .putArray("args", args);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Void) null));
                    return;
                }

                if ("ok".equals(status)) {
                    callback.handle(wrapResult(null, (Void) null));
                }
            }
        });
    }

    @Override
    public void save(final R record, final AsyncResultHandler<String> callback) {
        generateId(new AsyncResultHandler<String>() {
            @Override
            public void handle(final AsyncResult<String> generateId) {
                if (generateId.failed()) {
                    callback.handle(generateId);
                    return;
                }

                record.putString(ID, generateId.result());

                JsonArray args = new JsonArray().add(entity + SEP + generateId.result());

                try {
                    keyValues(args, record);
                } catch (RuntimeException re) {
                    callback.handle(wrapResult(re, (String) null));
                    return;
                }

                final JsonObject json = new JsonObject()
                        .putString("command", "hmset")
                        .putArray("args", args);

                eventBus.send(address, json, new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> event) {
                        final String status = event.body().getString("status");

                        if ("error".equals(status)) {
                            callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (String) null));
                            return;
                        }

                        if ("ok".equals(status)) {
                            callback.handle(wrapResult(null, generateId.result()));
                        }
                    }
                });
            }
        });
    }

    @Override
    public void findById(String id, AsyncResultHandler<R> callback) {
        findOne(id, callback);
    }

    @Override
    public void update(R record, AsyncResultHandler<Void> callback) {
        update(record.getId(), record, callback);
    }

    @Override
    public void remove(R record, AsyncResultHandler<Void> callback) {
        remove(record.getId(), callback);
    }

    @Override
    public void count(String query, final AsyncResultHandler<Number> callback) {
        JsonArray args = new JsonArray().add(entity + SEP + query);

        final JsonObject json = new JsonObject()
                .putString("command", "keys")
                .putArray("args", args);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Number) null));
                    return;
                }

                if ("ok".equals(status)) {
                    JsonArray keys = event.body().getArray("value");
                    callback.handle(wrapResult(null, (Number) keys.size()));
                }
            }
        });
    }

    @Override
    public void count(AsyncResultHandler<Number> callback) {
        count(ALL, callback);
    }

    @Override
    public void truncate(final AsyncResultHandler<Void> callback) {
        JsonArray args = new JsonArray().add(entity + SEP + ALL);

        final JsonObject json = new JsonObject()
                .putString("command", "keys")
                .putArray("args", args);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Void) null));
                    return;
                }

                if ("ok".equals(status)) {
                    JsonArray keys = event.body().getArray("value");

                    // perform real delete
                    final JsonObject json = new JsonObject()
                            .putString("command", "del")
                            .putArray("args", keys);

                    eventBus.send(address, json, new Handler<Message<JsonObject>>() {
                        @Override
                        public void handle(Message<JsonObject> event) {
                            final String status = event.body().getString("status");

                            if ("error".equals(status)) {
                                callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Void) null));
                                return;
                            }

                            if ("ok".equals(status)) {
                                callback.handle(wrapResult(null, (Void) null));
                            }
                        }
                    });
                }
            }
        });
    }

    private void generateId(final AsyncResultHandler<String> callback) {
        final JsonObject json = new JsonObject()
                    .putString("command", "incr")
                    .putArray("args", new JsonArray().add(entity));

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (String) null));
                    return;
                }

                if ("ok".equals(status)) {
                    // Ids have been returned
                    String id = event.body().getValue("value").toString();
                    callback.handle(wrapResult(null, id));
                }
            }
        });
    }
}
