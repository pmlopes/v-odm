package com.jetdrone.odm.backend;

import com.jetdrone.odm.Mapper;
import com.jetdrone.odm.Record;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public abstract class MongoDBMapper<R extends Record<String>> extends Mapper<String, R, JsonObject> {

    private static final JsonObject ALL = new JsonObject();

    public MongoDBMapper(EventBus eventBus, String address, String entity) {
        super(eventBus, address, entity, "_id");
    }

    @Override
    public void find(JsonObject query, final AsyncResultHandler<List<R>> callback) {
        final JsonObject json = new JsonObject()
                .putString("action", "find")
                .putString("collection", entity)
                .putObject("matcher", query);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            List<R> results = new ArrayList<>();

            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (List<R>) null));
                    return;
                }

                for (Object o : event.body().getArray("results")) {
                    if (o == null) {
                        results.add(null);
                    } else {
                        JsonObject json = (JsonObject) o;
                        results.add(newRecord(json));
                    }
                }

                if ("more-exist".equals(status)) {
                    // next batch
                    event.reply(this);
                } else {
                    callback.handle(wrapResult(null, results));
                }
            }
        });
    }

    @Override
    public void save(R record, final AsyncResultHandler<String> callback) {
        final JsonObject json = new JsonObject()
                .putString("action", "save")
                .putString("collection", entity)
                .putObject("document", record);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (String) null));
                    return;
                }

                if ("ok".equals(status)) {
                    callback.handle(wrapResult(null, event.body().getString("_id")));
                }
            }
        });
    }

    @Override
    public void findOne(JsonObject query, final AsyncResultHandler<R> callback) {
        final JsonObject json = new JsonObject()
                .putString("action", "findone")
                .putString("collection", entity)
                .putObject("matcher", query);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (R) null));
                    return;
                }

                callback.handle(wrapResult(null, newRecord(event.body().getObject("result"))));
            }
        });
    }

    @Override
    public void update(final JsonObject query, final R record, final AsyncResultHandler<Void> callback) {
        final JsonObject json = new JsonObject()
                .putString("action", "update")
                .putString("collection", entity)
                .putObject("criteria", query)
                .putObject("objNew", record);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Void) null));
                    return;
                }

                callback.handle(wrapResult(null, (Void) null));
            }
        });
    }

    @Override
    public void remove(JsonObject query, final AsyncResultHandler<Void> callback) {
        final JsonObject json = new JsonObject()
                .putString("action", "delete")
                .putString("collection", entity)
                .putObject("matcher", query);

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
    public void count(final JsonObject query, final AsyncResultHandler<Number> callback) {
        final JsonObject json = new JsonObject()
                .putString("action", "count")
                .putString("collection", entity)
                .putObject("matcher", query);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Number) null));
                    return;
                }

                callback.handle(wrapResult(null, event.body().getNumber("count")));
            }
        });
    }

    @Override
    public void truncate(final AsyncResultHandler<Void> callback) {
        final JsonObject json = new JsonObject()
                .putString("action", "dropCollection")
                .putString("collection", entity);

        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> event) {
                final String status = event.body().getString("status");

                if ("error".equals(status)) {
                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Void) null));
                    return;
                }

                callback.handle(wrapResult(null, (Void) null));
            }
        });
    }

    @Override
    public void update(final R record, final AsyncResultHandler<Void> callback) {
        update(new JsonObject().putString(ID, record.getId()), record, callback);
    }

    @Override
    public void remove(final R record, final AsyncResultHandler<Void> callback) {
        remove(new JsonObject().putString(ID, record.getId()), callback);
    }

    @Override
    public void findAll(final AsyncResultHandler<List<R>> callback) {
        find(ALL, callback);
    }

    @Override
    public void findById(String id, final AsyncResultHandler<R> callback) {
        findOne(new JsonObject().putString(ID, id), callback);
    }

    @Override
    public void count(final AsyncResultHandler<Number> callback) {
        count(ALL, callback);
    }
}
