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
  public void save(Map<String, Object> arg0, Handler<AsyncResult<String>> arg1) {
    this.delegate.save(arg0 != null ? new io.vertx.core.json.JsonObject(arg0) : null, arg1);
  }
  public void find(Map<String, Object> arg0, Handler<AsyncResult<List<Map<String, Object>>>> arg1) {
    this.delegate.find(arg0 != null ? new io.vertx.core.json.JsonObject(arg0) : null, new Handler<AsyncResult<List<JsonObject>>>() {
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
        arg1.handle(f)
      }
    });
  }
  public void update(Map<String, Object> arg0, Map<String, Object> arg1, Handler<AsyncResult<Void>> arg2) {
    this.delegate.update(arg0 != null ? new io.vertx.core.json.JsonObject(arg0) : null, arg1 != null ? new io.vertx.core.json.JsonObject(arg1) : null, arg2);
  }
  public void remove(Map<String, Object> arg0, Handler<AsyncResult<Void>> arg1) {
    this.delegate.remove(arg0 != null ? new io.vertx.core.json.JsonObject(arg0) : null, arg1);
  }
  public void count(Map<String, Object> arg0, Handler<AsyncResult<Long>> arg1) {
    this.delegate.count(arg0 != null ? new io.vertx.core.json.JsonObject(arg0) : null, arg1);
  }
  public void truncate(Handler<AsyncResult<Void>> arg0) {
    this.delegate.truncate(arg0);
  }
}
