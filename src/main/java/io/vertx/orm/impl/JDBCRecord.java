package io.vertx.orm.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.orm.SQLMapper;

public final class JDBCRecord extends SQLCommonRecord implements SQLMapper {

  private final JDBCClient jdbc;

  public JDBCRecord(JDBCClient jdbc, String entity) {
    super(entity);
    this.jdbc = jdbc;
  }

  @Override
  void getConnection(Handler<AsyncResult<SQLConnection>> handler) {
    jdbc.getConnection(handler);
  }
}
