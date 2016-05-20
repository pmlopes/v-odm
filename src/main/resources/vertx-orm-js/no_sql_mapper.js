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

/** @module vertx-orm-js/no_sql_mapper */
var utils = require('vertx-js/util/utils');
var MongoClient = require('vertx-mongo-js/mongo_client');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JNoSQLMapper = io.vertx.orm.NoSQLMapper;

/**
 @class
*/
var NoSQLMapper = function(j_val) {

  var j_noSQLMapper = j_val;
  var that = this;

  /**

   @public
   @param record {Object} 
   @param callback {function} 
   */
  this.save = function(record, callback) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_noSQLMapper["save(io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](utils.convParamJsonObject(record), function(ar) {
      if (ar.succeeded()) {
        callback(ar.result(), null);
      } else {
        callback(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param query {Object} 
   @param callback {function} 
   */
  this.find = function(query, callback) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_noSQLMapper["find(io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        callback(utils.convReturnListSetJson(ar.result()), null);
      } else {
        callback(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param query {Object} 
   @param record {Object} 
   @param callback {function} 
   */
  this.update = function(query, record, callback) {
    var __args = arguments;
    if (__args.length === 3 && (typeof __args[0] === 'object' && __args[0] != null) && (typeof __args[1] === 'object' && __args[1] != null) && typeof __args[2] === 'function') {
      j_noSQLMapper["update(io.vertx.core.json.JsonObject,io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](utils.convParamJsonObject(query), utils.convParamJsonObject(record), function(ar) {
      if (ar.succeeded()) {
        callback(null, null);
      } else {
        callback(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param query {Object} 
   @param callback {function} 
   */
  this.remove = function(query, callback) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_noSQLMapper["remove(io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        callback(null, null);
      } else {
        callback(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param query {Object} 
   @param callback {function} 
   */
  this.count = function(query, callback) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_noSQLMapper["count(io.vertx.core.json.JsonObject,io.vertx.core.Handler)"](utils.convParamJsonObject(query), function(ar) {
      if (ar.succeeded()) {
        callback(ar.result(), null);
      } else {
        callback(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param callback {function} 
   */
  this.truncate = function(callback) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_noSQLMapper["truncate(io.vertx.core.Handler)"](function(ar) {
      if (ar.succeeded()) {
        callback(null, null);
      } else {
        callback(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_noSQLMapper;
};

/**

 @memberof module:vertx-orm-js/no_sql_mapper
 @param mongo {MongoClient} 
 @param entity {string} 
 @return {NoSQLMapper}
 */
NoSQLMapper.create = function(mongo, entity) {
  var __args = arguments;
  if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._jdel && typeof __args[1] === 'string') {
    return utils.convReturnVertxGen(JNoSQLMapper["create(io.vertx.ext.mongo.MongoClient,java.lang.String)"](mongo._jdel, entity), NoSQLMapper);
  } else throw new TypeError('function invoked with invalid arguments');
};

// We export the Constructor function
module.exports = NoSQLMapper;