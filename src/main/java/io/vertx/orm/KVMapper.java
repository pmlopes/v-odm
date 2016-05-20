package io.vertx.orm;

import io.vertx.orm.impl.RedisRecord;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;

import java.util.List;

@VertxGen
public interface KVMapper {

    static KVMapper create(RedisClient redis, String entity) {
        return new RedisRecord(redis, entity);
    }

    // create
    void save(final JsonObject record, final Handler<AsyncResult<String>> callback);

    // read

    void find(final String query, final Handler<AsyncResult<List<JsonObject>>> callback);

    // update

    void update(final String query, final JsonObject record, final Handler<AsyncResult<Void>> callback);

    // delete

    void remove(final String query, final Handler<AsyncResult<Void>> callback);

    // utils

    void count(final String query, final Handler<AsyncResult<Long>> callback);

    void countAll(final Handler<AsyncResult<Long>> callback);

    void truncate(final Handler<AsyncResult<Void>> callback);
}
