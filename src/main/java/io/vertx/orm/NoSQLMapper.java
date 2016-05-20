package io.vertx.orm;

import io.vertx.orm.impl.MongoDBRecord;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

@VertxGen
public interface NoSQLMapper {

    static NoSQLMapper create(MongoClient mongo, String entity) {
        return new MongoDBRecord(mongo, entity);
    }

    // create
    void save(final JsonObject record, final Handler<AsyncResult<String>> callback);

    // read

    void find(final JsonObject query, final Handler<AsyncResult<List<JsonObject>>> callback);

    // update

    void update(final JsonObject query, final JsonObject record, final Handler<AsyncResult<Void>> callback);

    // delete

    void remove(final JsonObject query, final Handler<AsyncResult<Void>> callback);

    // utils

    void count(final JsonObject query, final Handler<AsyncResult<Long>> callback);

    void countAll(final Handler<AsyncResult<Long>> callback);

    void truncate(final Handler<AsyncResult<Void>> callback);
}
