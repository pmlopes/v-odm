package io.vertx.orm.impl;

import io.vertx.orm.KVMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;

import java.util.ArrayList;
import java.util.List;

public final class RedisRecord implements KVMapper {

  private static final String ALL = "*";
  private static final String SEP = ":";

  private final RedisClient redis;
  private final String entityName;
  private final String pkName;

  public RedisRecord(RedisClient redis, String entity) {
    this.redis = redis;
    this.entityName = entity;
    this.pkName = "id";
  }

  @Override
  public void find(final String query, final Handler<AsyncResult<List<JsonObject>>> callback) {
    redis.keys(entityName + SEP + query, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      final JsonArray keys = res1.result();

      // for each key get if from Redis
      final List<JsonObject> items = new ArrayList<>(keys.size());

      new AsyncIterator<Object>(keys) {
        @Override
        public void handle(Object key) {
          if (hasNext()) {
            redis.hgetall((String) key, res2 -> {
              if (res2.failed()) {
                callback.handle(Future.failedFuture(res2.cause()));
                return;
              }

              items.add(res2.result());
              next();
            });
          } else {
            callback.handle(Future.succeededFuture(items));
          }
        }
      };
    });
  }

  @Override
  public void update(String query, JsonObject record, final Handler<AsyncResult<Void>> callback) {
    redis.hmset(entityName + SEP + query, record, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      callback.handle(Future.succeededFuture());

    });
  }

  @Override
  public void remove(String query, final Handler<AsyncResult<Void>> callback) {
    redis.del(entityName + SEP + query, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      callback.handle(Future.succeededFuture());
    });
  }

  @Override
  public void save(final JsonObject record, final Handler<AsyncResult<String>> callback) {
    generateId(generateId -> {
      if (generateId.failed()) {
        callback.handle(generateId);
        return;
      }

      record.put(pkName, generateId.result());

      redis.hmset(entityName + SEP + generateId.result(), record, res1 -> {
        if (res1.failed()) {
          callback.handle(Future.failedFuture(res1.cause()));
          return;
        }

        callback.handle(Future.succeededFuture(generateId.result()));
      });
    });
  }

  @Override
  public void count(String query, final Handler<AsyncResult<Long>> callback) {
    redis.keys(entityName + SEP + query, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      callback.handle(Future.succeededFuture((long) res1.result().size()));
    });
  }

  @Override
  public void countAll(final Handler<AsyncResult<Long>> callback) {
    count(ALL, callback);
  }

  @Override
  public void truncate(final Handler<AsyncResult<Void>> callback) {
    redis.keys(entityName + SEP + ALL, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      // perform real delete
      redis.delMany(res1.result().getList(), res2 -> {
        if (res1.failed()) {
          callback.handle(Future.failedFuture(res2.cause()));
          return;
        }

        callback.handle(Future.succeededFuture());
      });
    });
  }

  private void generateId(final Handler<AsyncResult<String>> callback) {
    redis.incr(entityName, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }
      // Ids have been returned
      String id = res1.result().toString();
      callback.handle(Future.succeededFuture(id));
    });
  }
}
