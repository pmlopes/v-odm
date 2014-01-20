package com.jetdrone.odm;

import com.jetdrone.odm.backend.MongoDBMapper;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

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

    public MongoPersons(EventBus eventBus, String address) {
        super(eventBus, address, "users");
    }

    @Override
    protected Person newRecord(JsonObject json) {
        if (json == null) {
            return null;
        }

        return new Person(json.toMap());
    }
}