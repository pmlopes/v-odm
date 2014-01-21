package com.jetdrone.odm

import org.junit.Test

import org.vertx.groovy.core.eventbus.EventBus
import org.vertx.java.core.AsyncResult
import org.vertx.java.core.AsyncResultHandler
import org.vertx.java.core.Handler
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject

import org.vertx.testtools.TestVerticle

import static org.vertx.testtools.VertxAssert.*


class GPostgreSQLTest extends TestVerticle {

    final JsonObject config = new JsonObject()
            .putString("address", "postgresql.db")
            .putString("connection", "PostgreSQL")
            .putString("username", "postgres")
            //.putString("password", "pass")
            .putString("database", "odm");

    GPostgreSQLPersons mapper

    @Test
    void createSingleInstance() {
        // using the java inner class way
        Person person = mapper.newRecord()
        assertNotNull(person)
        testComplete()
    }

    @Test
    public void saveNewInstance() {
        // using the java inner class way
        Person person = mapper.newRecord()
        assertNotNull(person)

        // set some fields
        person["name"] = "Paulo"
        person["age"] = 33
        // there is no id
        assertNull(person[mapper.ID]);
        // save
        person.save() { saved ->
            if (!saved) {
                fail();
                return;
            }
            // user has been saved, the ID should be filled now
            assertNotNull(person.getField(mapper.ID));
            testComplete();
        }
    }

    @Override
    public void start() {
        initialize();
        final String address = config.getString("address");

        container.deployModule("io.vertx~mod-mysql-postgresql~0.2.0", config, new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
                if (asyncResult.failed()) {
                    container.logger().error(asyncResult.cause());
                }
                assertTrue(asyncResult.succeeded());
                assertNotNull("deploymentID should not be null", asyncResult.result());

                JsonObject initdb = new JsonObject()
                        .putString("action", "raw")
                        .putString("command", "create table if not exists psqlusers (id serial primary key, name varchar(255), age int);");

                getVertx().eventBus().send(address, initdb, new Handler<Message<JsonObject>>() {
                    @Override
                    public void handle(Message<JsonObject> event) {
                        assertEquals("ok", event.body().getString("status"));
                        GPostgreSQLTest.this.mapper = new GPostgreSQLPersons(new EventBus(getVertx().eventBus()), address);
                        startTests();
                    }
                });
            }
        });
    }
}
