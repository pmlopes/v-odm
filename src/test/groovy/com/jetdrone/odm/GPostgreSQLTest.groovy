package com.jetdrone.odm

import org.junit.Test

import org.vertx.groovy.core.Vertx
import org.vertx.groovy.platform.Container

import org.vertx.testtools.TestVerticle

import static org.vertx.testtools.VertxAssert.*

class GPostgreSQLTest extends TestVerticle {

    final Map config = [address:    "postgresql.db",
                        connection: "PostgreSQL",
                        username:   "postgres",
                        database:   "odm"]

    GPostgreSQLPersons mapper

    @Test
    void createSingleInstance() {
        // using the java inner class way
        Person person = mapper.newRecord()
        assertNotNull(person)
        testComplete()
    }

    @Test
    void saveNewInstance() {
        // using the java inner class way
        Person person = mapper.newRecord()
        assertNotNull(person)

        // set some fields
        person["name"] = "Paulo"
        person["age"] = 33
        // there is no id
        assertNull(person[mapper.ID])
        // save
        person.save() { saved ->
            if (!saved) {
                fail()
            }
            else {
                // user has been saved, the ID should be filled now
                assertNotNull(person[mapper.ID])
                testComplete()
            }
        }
    }

    @Override
    void start() {
        def gvertx = new Vertx( vertx )
        def gcontainer = new Container( container )
        initialize()
        final String address = config.address

        gcontainer.deployModule("io.vertx~mod-mysql-postgresql~0.2.0", config) { asyncResult ->
            if (asyncResult.failed()) {
                gcontainer.logger().error(asyncResult.cause())
            }
            assertTrue(asyncResult.succeeded())
            assertNotNull("deploymentID should not be null", asyncResult.result())

            Map initdb = [action: "raw",
                          command: "create table if not exists psqlusers (id serial primary key, name varchar(255), age int);"]

            gvertx.eventBus.send(address, initdb) { event ->
                assertEquals("ok", event.body.status)
                this.mapper = new GPostgreSQLPersons(gvertx.eventBus, address)
                startTests()
            }
        }
    }
}
