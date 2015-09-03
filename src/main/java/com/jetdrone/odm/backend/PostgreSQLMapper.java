//package com.jetdrone.odm.backend;
//
//import com.jetdrone.odm.Mapper;
//import com.jetdrone.odm.Record;
//import io.vertx.core.AsyncResultHandler;
//import io.vertx.core.Handler;
//import io.vertx.core.eventbus.EventBus;
//import io.vertx.core.eventbus.Message;
//import io.vertx.core.json.JsonArray;
//import io.vertx.core.json.JsonObject;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//public abstract class PostgreSQLMapper<R extends Record<Number>> extends Mapper<Number, R, String> {
//
//    private static final String ALL = "*";
//
//    public PostgreSQLMapper(EventBus eventBus, String address, String entity) {
//        super(eventBus, address, entity, "id");
//    }
//
//    private String fields(R record) {
//        StringBuilder sb = new StringBuilder();
//        Set<String> fields = record.getFieldNames();
//        if (fields != null) {
//            int len = fields.size();
//            for (String f : fields) {
//                sb.append(f);
//                if (0 != --len) {
//                    sb.append(", ");
//                }
//            }
//        }
//        return sb.toString();
//    }
//
//    private String sqlString(String s) {
//        StringBuilder str = new StringBuilder();
//        str.append('\'');
//        for (int i = 0; i < s.length(); i++) {
//            if (s.charAt(i) == '\\'
//                    || s.charAt(i) == '\"'
//                    || s.charAt(i) == '\'') {
//                str.append('\\');
//            }
//            str.append(s.charAt(i));
//        }
//        str.append('\'');
//
//        return str.toString();
//    }
//
//    private String values(R record) {
//        StringBuilder sb = new StringBuilder();
//        Set<String> fields = record.getFieldNames();
//        if (fields != null) {
//            int len = fields.size();
//            for (String f : fields) {
//                Object o = record.getValue(f);
//                if (o == null) {
//                    sb.append("null");
//                } else {
//                    if (o instanceof Boolean || o instanceof Number) {
//                        sb.append(o);
//                    } else if (o instanceof String) {
//                        sb.append(sqlString((String) o));
//                    } else {
//                        throw new RuntimeException("Unsupported value type: " + o.getClass().getName());
//                    }
//                }
//                if (0 != --len) {
//                    sb.append(", ");
//                }
//            }
//        }
//        return sb.toString();
//    }
//
//    private String set(R record) {
//        StringBuilder sb = new StringBuilder();
//        Set<String> fields = record.getFieldNames();
//        if (fields != null) {
//            // reduce ID since it will be skipped
//            int len = fields.size() - 1;
//            for (String f : fields) {
//                if (ID.equals(f)) {
//                    continue;
//                }
//
//                sb.append(f);
//                sb.append(" = ");
//
//                Object o = record.getValue(f);
//                if (o == null) {
//                    sb.append("null");
//                } else {
//                    if (o instanceof Boolean || o instanceof Number) {
//                        sb.append(o);
//                    } else if (o instanceof String) {
//                        sb.append(sqlString((String) o));
//                    } else {
//                        throw new RuntimeException("Unsupported value type: " + o.getClass().getName());
//                    }
//                }
//                if (0 != --len) {
//                    sb.append(", ");
//                }
//            }
//        }
//        return sb.toString();
//
//    }
//
//    @Override
//    public void find(final String query, final AsyncResultHandler<List<R>> callback) {
//        final JsonObject json = new JsonObject()
//                .putString("action", "raw")
//                .putString("command", "SELECT " + ALL + " FROM " + entity + " " + query);
//
//        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
//
//            List<R> retValue = new ArrayList<>();
//
//            @Override
//            public void handle(Message<JsonObject> event) {
//                final String status = event.body().getString("status");
//
//                if ("error".equals(status)) {
//                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (List<R>) null));
//                    return;
//                }
//
//                // convert from json back to R
//                JsonArray fields = event.body().getArray("fields");
//                JsonArray results = event.body().getArray("results");
//
//                for (Object result : results) {
//                    JsonArray row = (JsonArray) result;
//                    if (row != null) {
//                        JsonObject data = new JsonObject();
//                        for (int i = 0; i < fields.size(); i++) {
//                            data.putValue((String) fields.get(i), row.get(i));
//                        }
//                        retValue.add(newRecord(data));
//                    } else {
//                        retValue.add(null);
//                    }
//                }
//
//                callback.handle(wrapResult(null, retValue));
//            }
//        });
//    }
//
//    @Override
//    public void update(final String query, final R record, final AsyncResultHandler<Void> callback) {
//        final String sql;
//
//        try {
//            sql = "UPDATE " + entity + " SET " + set(record) + " " + query;
//        } catch (RuntimeException re) {
//            callback.handle(wrapResult(re, (Void) null));
//            return;
//        }
//
//        final JsonObject json = new JsonObject()
//                .putString("action", "raw")
//                .putString("command", sql);
//
//        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
//            @Override
//            public void handle(Message<JsonObject> event) {
//                final String status = event.body().getString("status");
//
//                if ("error".equals(status)) {
//                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Void) null));
//                    return;
//                }
//
//                callback.handle(wrapResult(null, (Void) null));
//            }
//        });
//    }
//
//    @Override
//    public void save(final R record, final AsyncResultHandler<Number> callback) {
//        final String sql;
//
//        try {
//            sql = "INSERT INTO " + entity + " (" + fields(record) + ") VALUES (" + values(record) + ") RETURNING " + ID;
//        } catch (RuntimeException re) {
//            callback.handle(wrapResult(re, (Number) null));
//            return;
//        }
//
//        final JsonObject json = new JsonObject()
//                .putString("action", "raw")
//                .putString("command", sql);
//
//        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
//            @Override
//            public void handle(Message<JsonObject> event) {
//                final String status = event.body().getString("status");
//
//                if ("error".equals(status)) {
//                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Number) null));
//                    return;
//                }
//
//                if ("ok".equals(status)) {
//                    // Ids have been returned
//                    Number id = null;
//                    if (event.body().getArray("fields").contains(ID)) {
//                        id = ((JsonArray) event.body().getArray("results").get(0)).get(0);
//                    }
//                    callback.handle(wrapResult(null, id));
//                }
//            }
//        });
//    }
//
//    @Override
//    public void findOne(final String query, final AsyncResultHandler<R> callback) {
//        final JsonObject json = new JsonObject()
//                .putString("action", "raw")
//                .putString("command", "SELECT " + ALL + " FROM " + entity + " " + query);
//
//        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
//            @Override
//            public void handle(Message<JsonObject> event) {
//                final String status = event.body().getString("status");
//
//                if ("error".equals(status)) {
//                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (R) null));
//                    return;
//                }
//
//                // convert from json back to R
//                JsonArray fields = event.body().getArray("fields");
//                JsonArray results = event.body().getArray("results");
//
//                JsonObject data = null;
//
//                if (results.size() >= 1) {
//                    JsonArray row = results.get(0);
//                    if (row != null) {
//                        data = new JsonObject();
//                        for (int i = 0; i < fields.size(); i++) {
//                            data.putValue((String) fields.get(i), row.get(i));
//                        }
//                    }
//                }
//
//                callback.handle(wrapResult(null, newRecord(data)));
//            }
//        });
//    }
//
//    @Override
//    public void remove(final String query, final AsyncResultHandler<Void> callback) {
//        final JsonObject json = new JsonObject()
//                .putString("action", "raw")
//                .putString("command", "DELETE FROM " + entity + " " + query);
//
//        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
//            @Override
//            public void handle(Message<JsonObject> event) {
//                final String status = event.body().getString("status");
//
//                if ("error".equals(status)) {
//                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Void) null));
//                    return;
//                }
//
//                if ("ok".equals(status)) {
//                    callback.handle(wrapResult(null, (Void) null));
//                }
//            }
//        });
//    }
//
//    @Override
//    public void count(final String query, final AsyncResultHandler<Number> callback) {
//        final JsonObject json = new JsonObject()
//                .putString("action", "raw")
//                .putString("command", "SELECT COUNT(*) FROM " + entity + " " + query);
//
//        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
//            @Override
//            public void handle(Message<JsonObject> event) {
//                final String status = event.body().getString("status");
//
//                if ("error".equals(status)) {
//                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Number) null));
//                    return;
//                }
//
//                // counts have been returned
//                Number count = null;
//                if (event.body().getArray("fields").contains("count")) {
//                    count = ((JsonArray) event.body().getArray("results").get(0)).get(0);
//                }
//
//                callback.handle(wrapResult(null, count));
//            }
//        });
//    }
//
//    @Override
//    public void truncate(final AsyncResultHandler<Void> callback) {
//        final JsonObject json = new JsonObject()
//                .putString("action", "raw")
//                .putString("command", "TRUNCATE " + entity + " RESTART IDENTITY");
//
//        eventBus.send(address, json, new Handler<Message<JsonObject>>() {
//            @Override
//            public void handle(Message<JsonObject> event) {
//                final String status = event.body().getString("status");
//
//                if ("error".equals(status)) {
//                    callback.handle(wrapResult(new RuntimeException(event.body().getString("message")), (Void) null));
//                    return;
//                }
//
//                callback.handle(wrapResult(null, (Void) null));
//            }
//        });
//    }
//
//    @Override
//    public void update(final R record, final AsyncResultHandler<Void> callback) {
//        update("WHERE " + ID + " = " + record.getId(), record, callback);
//    }
//
//    @Override
//    public void remove(final R record, final AsyncResultHandler<Void> callback) {
//        remove("WHERE " + ID + " = " + record.getId(), callback);
//    }
//
//    @Override
//    public void findById(Number id, final AsyncResultHandler<R> callback) {
//        findOne("WHERE " + ID + " = " + id, callback);
//    }
//
//    @Override
//    public void findAll(final AsyncResultHandler<List<R>> callback) {
//        find(ALL, callback);
//    }
//
//    @Override
//    public void count(final AsyncResultHandler<Number> callback) {
//        count("", callback);
//    }
//}
