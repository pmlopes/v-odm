package com.jetdrone.odm.backend;

import com.jetdrone.odm.Mapper;
import com.jetdrone.odm.Record;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;
import java.util.List;

public abstract class MongoDBMapper<R extends Record<String>> extends Mapper<String, R, JsonObject> {

  private static final JsonObject ALL = new JsonObject();

  private final MongoClient mongo;

  public MongoDBMapper(MongoClient mongo, String entity) {
    super(entity, "_id");
    this.mongo = mongo;
  }

  @Override
  public void find(JsonObject query, final Handler<AsyncResult<List<R>>> callback) {
    mongo.find(entity, query, res -> {
      if (res.failed()) {
        callback.handle(Future.failedFuture(res.cause()));
        return;
      }

      final List<R> results = new ArrayList<>();

      for (JsonObject doc : res.result()) {
        if (doc == null) {
          results.add(null);
        } else {
          results.add(newRecord(doc));
        }
      }

      callback.handle(Future.succeededFuture(results));
    });
  }

  @Override
  public void save(R record, final Handler<AsyncResult<String>> callback) {
    mongo.save(entity, new JsonObject(record.getMap()), callback);
  }

  @Override
  public void findOne(JsonObject query, final Handler<AsyncResult<R>> callback) {
    mongo.findOne(entity, query, ALL, res -> {
      if (res.failed()) {
        callback.handle(Future.failedFuture(res.cause()));
        return;
      }

      final JsonObject doc = res.result();

      if (doc == null) {
        callback.handle(Future.succeededFuture(newRecord(null)));
      } else {
        callback.handle(Future.succeededFuture(newRecord(doc)));
      }
    });
  }

  @Override
  public void update(final JsonObject query, final R record, final Handler<AsyncResult<Void>> callback) {
    mongo.replace(entity, query, new JsonObject(record.getMap()), callback);
  }

  @Override
  public void remove(JsonObject query, final Handler<AsyncResult<Void>> callback) {
    mongo.remove(entity, query, callback);
  }

  @Override
  public void count(final JsonObject query, final Handler<AsyncResult<Long>> callback) {
    mongo.count(entity, query, callback);
  }

  @Override
  public void truncate(final Handler<AsyncResult<Void>> callback) {
    mongo.dropCollection(entity, callback);
  }

  @Override
  public void update(final R record, final Handler<AsyncResult<Void>> callback) {
    update(new JsonObject().put(ID, record.getId()), record, callback);
  }

  @Override
  public void remove(final R record, final Handler<AsyncResult<Void>> callback) {
    remove(new JsonObject().put(ID, record.getId()), callback);
  }

  @Override
  public void findAll(final Handler<AsyncResult<List<R>>> callback) {
    find(ALL, callback);
  }

  @Override
  public void findById(String id, final Handler<AsyncResult<R>> callback) {
    findOne(new JsonObject().put(ID, id), callback);
  }

  @Override
  public void count(final Handler<AsyncResult<Long>> callback) {
    count(ALL, callback);
  }
}
