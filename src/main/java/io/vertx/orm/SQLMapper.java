package io.vertx.orm;

import io.vertx.orm.impl.AsyncSQLRecord;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.orm.impl.JDBCRecord;
import org.intellij.lang.annotations.Language;

import java.util.List;

@VertxGen
public interface SQLMapper {

    static SQLMapper create(JDBCClient jdbc, String entity) {
        return new JDBCRecord(jdbc, entity);
    }

    static SQLMapper createAsync(AsyncSQLClient jdbc, String entity) {
        return new AsyncSQLRecord(jdbc, entity);
    }

    // create
    void save(final JsonObject record, final Handler<AsyncResult<Long>> callback);

    // read

    void find(final @Language("SQL") String query, final Handler<AsyncResult<List<JsonObject>>> callback);

    // update

    void update(final @Language("SQL") String query, final JsonObject record, final Handler<AsyncResult<Integer>> callback);

    // delete

    void remove(final @Language("SQL") String query, final Handler<AsyncResult<Void>> callback);

    // utils

    void count(final @Language("SQL") String query, final Handler<AsyncResult<Long>> callback);

    void countAll(final Handler<AsyncResult<Long>> callback);

    void truncate(final Handler<AsyncResult<Void>> callback);
}
