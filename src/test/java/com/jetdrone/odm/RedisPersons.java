package com.jetdrone.odm;

import com.jetdrone.odm.backend.RedisMapper;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

public class RedisPersons extends RedisMapper<RedisPersons.Person> {

    public class Person extends Record<String> {
        public Person() {
            super(RedisPersons.this);
        }

        public Person(Map<String, Object> map) {
            super(RedisPersons.this, map);
        }

        public String sayHello() {
            return "Hello " + getString("name");
        }
    }

    public RedisPersons(EventBus eventBus, String address) {
        super(eventBus, address, "person");
    }

    @Override
    protected Person newRecord(JsonObject json) {
        if (json == null) {
            return null;
        }

        return new Person(json.toMap());
    }
}