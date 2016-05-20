package io.vertx.orm.impl;

import io.vertx.ext.sql.SQLConnection;
import io.vertx.orm.SQLMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.asyncsql.AsyncSQLClient;

public final class AsyncSQLRecord extends SQLCommonRecord implements SQLMapper {

  private final AsyncSQLClient asyncSQL;

  public AsyncSQLRecord(AsyncSQLClient asyncSQL, String entity) {
    super(entity);
    this.asyncSQL = asyncSQL;
  }

  @Override
  void getConnection(Handler<AsyncResult<SQLConnection>> handler) {
    asyncSQL.getConnection(handler);
  }
}
