package com.jetdrone.odm;

import com.jetdrone.odm.backend.PostgreSQLMapper;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

public class PostgreSQLPersons extends PostgreSQLMapper<PostgreSQLPersons.Person> {

    public class Person extends Record<Number> {
        public Person() {
            super(PostgreSQLPersons.this);
        }

        public Person(Map<String, Object> map) {
            super(PostgreSQLPersons.this, map);
        }

        public String sayHello() {
            return "Hello " + getString("name");
        }
    }

    public PostgreSQLPersons(EventBus eventBus, String address) {
        super(eventBus, address, "psqlusers");
    }

    @Override
    protected Person newRecord(JsonObject json) {
        if (json == null) {
            return null;
        }

        return new Person(json.toMap());
    }

}