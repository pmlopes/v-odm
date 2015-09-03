package com.jetdrone.odm.backend;

import com.jetdrone.odm.Mapper;
import com.jetdrone.odm.Record;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class JdbcMapper<R extends Record<Number>> extends Mapper<Number, R, String> {

  private static final String ALL = "*";

  private final JDBCClient jdbc;

  public JdbcMapper(JDBCClient jdbc, String entity) {
    super(entity, "ID");
    this.jdbc = jdbc;
  }

  private String fields(R record) {
    StringBuilder sb = new StringBuilder();
    Set<String> fields = record.fieldNames();
    if (fields != null) {
      int len = fields.size();
      for (String f : fields) {
        sb.append(f);
        if (0 != --len) {
          sb.append(", ");
        }
      }
    }
    return sb.toString();
  }

  private String placeholders(R record, boolean names) {
    StringBuilder sb = new StringBuilder();
    Set<String> fields = record.fieldNames();
    if (fields != null) {
      int len = fields.size();
      for (String f : fields) {
        if (names) {
          sb.append(f).append("=");
        }
        sb.append("?");
        if (0 != --len) {
          sb.append(", ");
        }
      }
    }
    return sb.toString();
  }

  private JsonArray values(R record) {
    List<Object> ret = new ArrayList<>();
    Set<String> fields = record.fieldNames();
    if (fields != null) {
      ret.addAll(fields.stream().map(record::getValue).collect(Collectors.toList()));
    }
    return new JsonArray(ret);
  }

  @Override
  public void find(@Language("SQL") final String query, final Handler<AsyncResult<List<R>>> callback) {
    jdbc.getConnection(res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      res1.result().query("SELECT " + ALL + " FROM " + entity + " " + query, res2 -> {
        if (res2.failed()) {
          callback.handle(Future.failedFuture(res2.cause()));
          return;
        }

        List<R> retValue = new ArrayList<>();

        // convert from json back to R
        retValue.addAll(res2.result().getRows().stream().map(this::newRecord).collect(Collectors.toList()));

        callback.handle(Future.succeededFuture(retValue));
      });
    });
  }

  @Override
  public void update(@Language("SQL") final String query, final R record, final Handler<AsyncResult<Void>> callback) {
    final String sql;

    try {
      sql = "UPDATE " + entity + " SET " + placeholders(record, true) + " " + query;
    } catch (RuntimeException re) {
      callback.handle(Future.failedFuture(re));
      return;
    }

    jdbc.getConnection(res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      res1.result().updateWithParams(sql, values(record), res2 -> {
        if (res2.failed()) {
          callback.handle(Future.failedFuture(res2.cause()));
          return;
        }

        callback.handle(Future.succeededFuture());
      });
    });
  }

  @Override
  public void save(final R record, final Handler<AsyncResult<Number>> callback) {
    final String sql;

    try {
      sql = "INSERT INTO " + entity + " (" + fields(record) + ") VALUES (" + placeholders(record, false) + ")";
    } catch (RuntimeException re) {
      callback.handle(Future.failedFuture(re));
      return;
    }

    jdbc.getConnection(res1 -> {
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
        callback.handle(Future.succeededFuture((Number) res2.result().getKeys().getValue(0)));
      });
    });
  }

  @Override
  public void findOne(@Language("SQL") final String query, final Handler<AsyncResult<R>> callback) {
    jdbc.getConnection(res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      res1.result().query("SELECT " + ALL + " FROM " + entity + " " + query, res2 -> {
        if (res2.failed()) {
          callback.handle(Future.failedFuture(res2.cause()));
          return;
        }

        // convert from json back to R
        final List<JsonObject> results = res2.result().getRows();

        if (results.size() > 0) {
          callback.handle(Future.succeededFuture(newRecord(results.get(0))));
        } else {
          callback.handle(Future.succeededFuture(null));
        }
      });
    });
  }

  @Override
  public void remove(@Language("SQL") final String query, final Handler<AsyncResult<Void>> callback) {
    jdbc.getConnection(res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      res1.result().update("DELETE FROM " + entity + " " + query, res2 -> {
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
    jdbc.getConnection(res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      res1.result().query("SELECT COUNT(*) AS CNT FROM " + entity + " " + query, res2 -> {
        if (res2.failed()) {
          callback.handle(Future.failedFuture(res2.cause()));
          return;
        }

        // convert from json back to R
        final List<JsonObject> results = res2.result().getRows();

        callback.handle(Future.succeededFuture(results.get(0).getLong("CNT")));
      });
    });
  }

  @Override
  public void truncate(final Handler<AsyncResult<Void>> callback) {
    jdbc.getConnection(res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      res1.result().execute("TRUNCATE TABLE " + entity, res2 -> {
        if (res2.failed()) {
          callback.handle(Future.failedFuture(res2.cause()));
          return;
        }
        callback.handle(Future.succeededFuture());
      });
    });
  }

  @Override
  public void update(final R record, final Handler<AsyncResult<Void>> callback) {
    update("WHERE " + ID + " = " + record.getId(), record, callback);
  }

  @Override
  public void remove(final R record, final Handler<AsyncResult<Void>> callback) {
    remove("WHERE " + ID + " = " + record.getId(), callback);
  }

  @Override
  public void findById(Number id, final Handler<AsyncResult<R>> callback) {
    findOne("WHERE " + ID + " = " + id, callback);
  }

  @Override
  public void findAll(final Handler<AsyncResult<List<R>>> callback) {
    find("", callback);
  }

  @Override
  public void count(final Handler<AsyncResult<Long>> callback) {
    count("", callback);
  }
}
