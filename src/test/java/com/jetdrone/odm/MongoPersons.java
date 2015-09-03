package com.jetdrone.odm;

import com.jetdrone.odm.backend.MongoDBMapper;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.Map;

public class MongoPersons extends MongoDBMapper<MongoPersons.Person> {

    public class Person extends Record<String> {
        public Person() {
            super(MongoPersons.this);
        }

        public Person(Map<String, Object> map) {
            super(MongoPersons.this, map);
        }

        public String sayHello() {
            return "Hello " + getString("name");
        }
    }

    public MongoPersons(MongoClient mongo) {
        super(mongo, "users");
    }

    @Override
    protected Person newRecord(JsonObject json) {
        if (json == null) {
            return null;
        }

        return new Person(json.getMap());
    }
}