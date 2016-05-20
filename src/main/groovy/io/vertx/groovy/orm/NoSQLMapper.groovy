/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.groovy.orm;
import groovy.transform.CompileStatic
import io.vertx.lang.groovy.InternalHelper
import io.vertx.core.json.JsonObject
import java.util.List
import io.vertx.groovy.ext.mongo.MongoClient
import io.vertx.core.json.JsonObject
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
@CompileStatic
public class NoSQLMapper {
  private final def io.vertx.orm.NoSQLMapper delegate;
  public NoSQLMapper(Object delegate) {
    this.delegate = (io.vertx.orm.NoSQLMapper) delegate;
  }
  public Object getDelegate() {
    return delegate;
  }
  public static NoSQLMapper create(MongoClient mongo, String entity) {
    def ret= InternalHelper.safeCreate(io.vertx.orm.NoSQLMapper.create((io.vertx.ext.mongo.MongoClient)mongo.getDelegate(), entity), io.vertx.groovy.orm.NoSQLMapper.class);
    return ret;
  }
  public void save(Map<String, Object> record, Handler<AsyncResult<String>> callback) {
    this.delegate.save(record != null ? new io.vertx.core.json.JsonObject(record) : null, callback);
  }
  public void find(Map<String, Object> query, Handler<AsyncResult<List<Map<String, Object>>>> callback) {
    this.delegate.find(query != null ? new io.vertx.core.json.JsonObject(query) : null, new Handler<AsyncResult<List<JsonObject>>>() {
      public void handle(AsyncResult<List<JsonObject>> event) {
        AsyncResult<List<Map<String, Object>>> f
        if (event.succeeded()) {
          f = InternalHelper.<List<Map<String, Object>>>result(event.result().collect({
            io.vertx.core.json.JsonObject element ->
            InternalHelper.wrapObject(element)
          }) as List)
        } else {
          f = InternalHelper.<List<Map<String, Object>>>failure(event.cause())
        }
        callback.handle(f)
      }
    });
  }
  public void update(Map<String, Object> query, Map<String, Object> record, Handler<AsyncResult<Void>> callback) {
    this.delegate.update(query != null ? new io.vertx.core.json.JsonObject(query) : null, record != null ? new io.vertx.core.json.JsonObject(record) : null, callback);
  }
  public void remove(Map<String, Object> query, Handler<AsyncResult<Void>> callback) {
    this.delegate.remove(query != null ? new io.vertx.core.json.JsonObject(query) : null, callback);
  }
  public void count(Map<String, Object> query, Handler<AsyncResult<Long>> callback) {
    this.delegate.count(query != null ? new io.vertx.core.json.JsonObject(query) : null, callback);
  }
  public void truncate(Handler<AsyncResult<Void>> callback) {
    this.delegate.truncate(callback);
  }
}
