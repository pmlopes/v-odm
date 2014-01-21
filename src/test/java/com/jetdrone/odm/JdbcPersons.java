package com.jetdrone.odm;

import com.jetdrone.odm.backend.JdbcMapper;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

public class JdbcPersons extends JdbcMapper<JdbcPersons.Person> {

    public class Person extends Record<Number> {
        public Person() {
            super(JdbcPersons.this);
        }

        public Person(Map<String, Object> map) {
            super(JdbcPersons.this, map);
        }

        public String sayHello() {
            return "Hello " + getString("NAME");
        }
    }

    public JdbcPersons(EventBus eventBus, String address) {
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