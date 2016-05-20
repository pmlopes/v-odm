package io.vertx.orm.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.orm.SQLMapper;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

abstract class SQLCommonRecord implements SQLMapper {

    private static final String ALL = "*";

    private final String entityName;

    abstract void getConnection(Handler<AsyncResult<SQLConnection>> handler);

    public SQLCommonRecord(String entity) {
        this.entityName = entity;
    }

    private static String fields(JsonObject record) {
        StringBuilder sb = new StringBuilder();
        Set<String> fields = record.fieldNames();
        if (fields != null) {
            int len = fields.size();
            for (String f : fields) {
                sb.append(snake(f));
                if (0 != --len) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    private static String placeholders(JsonObject record, boolean names) {
        StringBuilder sb = new StringBuilder();
        Set<String> fields = record.fieldNames();
        if (fields != null) {
            int len = fields.size();
            for (String f : fields) {
                if (names) {
                    sb.append(snake(f)).append("=");
                }
                sb.append("?");
                if (0 != --len) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    private static JsonArray values(JsonObject record) {
        List<Object> ret = new ArrayList<>();
        Set<String> fields = record.fieldNames();
        if (fields != null) {
            ret.addAll(fields.stream().map(record::getValue).collect(Collectors.toList()));
        }
        return new JsonArray(ret);
    }

    private static String snake(String string) {
        final StringBuilder sb = new StringBuilder(string.length() + 2);

        char last = 0;
        for (char c : string.toCharArray()) {
            if (Character.isUpperCase(c) || (Character.isDigit(c) && !Character.isDigit(last) && last != '_')) {
                sb.append('_');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
            last = c;
        }

        return sb.toString();
    }

    private static String camel(String string) {
        final StringBuilder sb = new StringBuilder(string.length());

        char last = 0;
        for (char c : string.toLowerCase().toCharArray()) {
            if (last == '_' && !Character.isDigit(last)) {
                sb.setLength(sb.length() -1);
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
            last = c;
        }

        return sb.toString();
    }

    /**
     * Some magic will happen here, column names will be replaced with camel case versions
     */
    private static List<JsonObject> toRows(final ResultSet resultSet) {
        final List<JsonObject> rows = new ArrayList<>(resultSet.getNumRows());
        final int cols = resultSet.getColumnNames().size();

        for (final JsonArray result: resultSet.getResults()) {
            final JsonObject row = new JsonObject();
            for (int i = 0; i < cols; i++) {
                final String key = camel(resultSet.getColumnNames().get(i));
                if (row.containsKey(key)) {
                    final Object old = row.getValue(key);

                    if (old instanceof JsonArray) {
                        ((JsonArray) old).add(result.getValue(i));
                    } else {
                        row.put(key, new JsonArray().add(old));
                    }
                } else {
                    row.put(key, result.getValue(i));
                }
            }
            rows.add(row);
        }

        return rows;
    }


    @Override
    public void find(@Language("SQL") final String query, final Handler<AsyncResult<List<JsonObject>>> callback) {
        getConnection(res1 -> {
            if (res1.failed()) {
                callback.handle(Future.failedFuture(res1.cause()));
                return;
            }

            res1.result().query("SELECT " + ALL + " FROM " + entityName + " " + query, res2 -> {
                if (res2.failed()) {
                    callback.handle(Future.failedFuture(res2.cause()));
                    return;
                }

                callback.handle(Future.succeededFuture(toRows(res2.result())));
            });
        });
    }

    @Override
    public void update(@Language("SQL") final String query, final JsonObject record, final Handler<AsyncResult<Integer>> callback) {
        final String sql;

        try {
            sql = "UPDATE " + entityName + " SET " + placeholders(record, true) + " " + query;
        } catch (RuntimeException re) {
            callback.handle(Future.failedFuture(re));
            return;
        }

        getConnection(res1 -> {
            if (res1.failed()) {
                callback.handle(Future.failedFuture(res1.cause()));
                return;
            }

            res1.result().updateWithParams(sql, values(record), res2 -> {
                if (res2.failed()) {
                    callback.handle(Future.failedFuture(res2.cause()));
                    return;
                }

                callback.handle(Future.succeededFuture(res2.result().getUpdated()));
            });
        });
    }

    @Override
    public void save(final JsonObject record, final Handler<AsyncResult<Long>> callback) {
        final String sql;

        try {
            sql = "INSERT INTO " + entityName + " (" + fields(record) + ") VALUES (" + placeholders(record, false) + ")";
        } catch (RuntimeException re) {
            callback.handle(Future.failedFuture(re));
            return;
        }

        getConnection(res1 -> {
            if (res1.failed()) {
                callback.handle(Future.failedFuture(res1.cause()));
                return;
            }

            res1.result().updateWithParams(sql, values(record), res2 -> {
                if (res2.failed()) {
                    callback.handle(Future.failedFuture(res2.cause()));
                    return;
                }

                // Ids have been returned
                callback.handle(Future.succeededFuture(res2.result().getKeys().getLong(0)));
            });
        });
    }

    @Override
    public void remove(@Language("SQL") final String query, final Handler<AsyncResult<Void>> callback) {
        getConnection(res1 -> {
            if (res1.failed()) {
                callback.handle(Future.failedFuture(res1.cause()));
                return;
            }

            res1.result().update("DELETE FROM " + entityName + " " + query, res2 -> {
                if (res2.failed()) {
                    callback.handle(Future.failedFuture(res2.cause()));
                    return;
                }
                callback.handle(Future.succeededFuture());
            });
        });
    }

    @Override
    public void count(@Language("SQL") final String query, final Handler<AsyncResult<Long>> callback) {
        getConnection(res1 -> {
            if (res1.failed()) {
                callback.handle(Future.failedFuture(res1.cause()));
                return;
            }

            res1.result().query("SELECT COUNT(*) AS cnt FROM " + entityName + " " + query, res2 -> {
                if (res2.failed()) {
                    callback.handle(Future.failedFuture(res2.cause()));
                    return;
                }

                final List<JsonObject> results = toRows(res2.result());

                callback.handle(Future.succeededFuture(results.get(0).getLong("cnt")));
            });
        });
    }

    @Override
    public void countAll(final Handler<AsyncResult<Long>> callback) {
        count("", callback);
    }

    @Override
    public void truncate(final Handler<AsyncResult<Void>> callback) {
        getConnection(res1 -> {
            if (res1.failed()) {
                callback.handle(Future.failedFuture(res1.cause()));
                return;
            }

            res1.result().execute("TRUNCATE TABLE " + entityName, res2 -> {
                if (res2.failed()) {
                    callback.handle(Future.failedFuture(res2.cause()));
                    return;
                }
                callback.handle(Future.succeededFuture());
            });
        });
    }
}
