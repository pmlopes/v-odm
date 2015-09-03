package com.jetdrone.odm;

import com.jetdrone.odm.backend.JdbcMapper;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

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

    public JdbcPersons(JDBCClient jdbc) {
        super(jdbc, "users");
    }

    @Override
    protected Person newRecord(JsonObject json) {
        if (json == null) {
            return null;
        }

        return new Person(json.getMap());
    }

}