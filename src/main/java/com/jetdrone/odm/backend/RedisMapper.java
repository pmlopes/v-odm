package com.jetdrone.odm.backend;

import com.jetdrone.odm.Mapper;
import com.jetdrone.odm.Record;
import com.jetdrone.odm.backend.impl.AsyncIterator;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.redis.RedisClient;

import java.util.ArrayList;
import java.util.List;

public abstract class RedisMapper<R extends Record<String>> extends Mapper<String, R, String> {

  private static final String ALL = "*";
  private static final String SEP = ":";

  private final RedisClient redis;

  public RedisMapper(RedisClient redis, String entity) {
    this(redis, entity, "id");
  }

  public RedisMapper(RedisClient redis, String entity, String id) {
    super(entity, id);
    this.redis = redis;
  }

  @Override
  public void find(final String query, final Handler<AsyncResult<List<R>>> callback) {
    redis.keys(entity + SEP + query, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      final JsonArray keys = res1.result();

      // for each key get if from Redis
      final List<R> items = new ArrayList<>(keys.size());

      new AsyncIterator<Object>(keys) {
        @Override
        public void handle(Object key) {
          if (hasNext()) {
            redis.hgetall(entity + SEP + key, res2 -> {
              if (res2.failed()) {
                callback.handle(Future.failedFuture(res2.cause()));
                return;
              }

              items.add(newRecord(res2.result()));
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
  public void findOne(String query, final Handler<AsyncResult<R>> callback) {
    redis.hgetall(entity + SEP + query, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      callback.handle(Future.succeededFuture(newRecord(res1.result())));
    });
  }

  @Override
  public void findAll(Handler<AsyncResult<List<R>>> callback) {
    find(ALL, callback);
  }

  @Override
  public void update(String query, R record, final Handler<AsyncResult<Void>> callback) {
    redis.hmset(entity + SEP + query, record, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      callback.handle(Future.succeededFuture());

    });
  }

  @Override
  public void remove(String query, final Handler<AsyncResult<Void>> callback) {
    redis.del(entity + SEP + query, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      callback.handle(Future.succeededFuture());
    });
  }

  @Override
  public void save(final R record, final Handler<AsyncResult<String>> callback) {
    generateId(generateId -> {
      if (generateId.failed()) {
        callback.handle(generateId);
        return;
      }

      record.put(ID, generateId.result());

      redis.hmset(entity + SEP + generateId.result(), record, res1 -> {
        if (res1.failed()) {
          callback.handle(Future.failedFuture(res1.cause()));
          return;
        }

        callback.handle(Future.succeededFuture(generateId.result()));
      });
    });
  }

  @Override
  public void findById(String id, Handler<AsyncResult<R>> callback) {
    findOne(id, callback);
  }

  @Override
  public void update(R record, Handler<AsyncResult<Void>> callback) {
    update(record.getId(), record, callback);
  }

  @Override
  public void remove(R record, Handler<AsyncResult<Void>> callback) {
    remove(record.getId(), callback);
  }

  @Override
  public void count(String query, final Handler<AsyncResult<Long>> callback) {
    redis.keys(entity + SEP + query, res1 -> {
      if (res1.failed()) {
        callback.handle(Future.failedFuture(res1.cause()));
        return;
      }

      callback.handle(Future.succeededFuture((long) res1.result().size()));
    });
  }

  @Override
  public void count(Handler<AsyncResult<Long>> callback) {
    count(ALL, callback);
  }

  @Override
  public void truncate(final Handler<AsyncResult<Void>> callback) {
    redis.keys(entity + SEP + ALL, res1 -> {
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
    redis.incr(entity, res1 -> {
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
