package io.vertx.orm.impl;

import io.vertx.orm.NoSQLMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

public final class MongoDBRecord implements NoSQLMapper {

  private final MongoClient mongo;
  private final String entityName;

  public MongoDBRecord(MongoClient mongo, String entity) {
    this.mongo = mongo;
    this.entityName = entity;
  }

  @Override
  public void find(JsonObject query, final Handler<AsyncResult<List<JsonObject>>> callback) {
    mongo.find(entityName, query, res -> {
      if (res.failed()) {
        callback.handle(Future.failedFuture(res.cause()));
        return;
      }

      callback.handle(Future.succeededFuture(res.result()));
    });
  }

  @Override
  public void save(JsonObject record, final Handler<AsyncResult<String>> callback) {
    mongo.save(entityName, new JsonObject(record.getMap()), callback);
  }

  @Override
  public void update(final JsonObject query, final JsonObject record, final Handler<AsyncResult<Void>> callback) {
    mongo.replace(entityName, query, new JsonObject(record.getMap()), callback);
  }

  @Override
  public void remove(JsonObject query, final Handler<AsyncResult<Void>> callback) {
    mongo.remove(entityName, query, callback);
  }

  @Override
  public void count(final JsonObject query, final Handler<AsyncResult<Long>> callback) {
    mongo.count(entityName, query, callback);
  }

  @Override
  public void countAll(final Handler<AsyncResult<Long>> callback) {
    mongo.count(entityName, new JsonObject(), callback);
  }

  @Override
  public void truncate(final Handler<AsyncResult<Void>> callback) {
    mongo.dropCollection(entityName, callback);
  }
}
