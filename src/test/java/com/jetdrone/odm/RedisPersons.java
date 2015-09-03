package com.jetdrone.odm;

import com.jetdrone.odm.backend.RedisMapper;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;

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

    public RedisPersons(RedisClient client) {
        super(client, "person");
    }

    @Override
    protected Person newRecord(JsonObject json) {
        if (json == null) {
            return null;
        }

        return new Person(json.getMap());
    }
}