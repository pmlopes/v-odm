package com.jetdrone.odm;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import com.jetdrone.odm.RedisPersons.Person;

import static org.vertx.testtools.VertxAssert.*;

public class RedisTest extends TestVerticle {

    final JsonObject config = new JsonObject()
            .putString("address", "redis.db");

    private RedisPersons mapper;

    @Test
    public void createSingleInstance() {
        // using the java inner class way
        Person person = mapper.new Person();
        assertNotNull(person);
        testComplete();
    }

    @Test
    public void saveNewInstance() {
        // using the java inner class way
        final Person person = mapper.new Person();
        assertNotNull(person);

        // set some fields
        person.putString("name", "Paulo");
        person.putNumber("age", 33);
        // there is no _id
        assertNull(person.getField(mapper.ID));
        // save
        person.save(new Handler<Boolean>() {
            @Override
            public void handle(Boolean saved) {
                if (!saved) {
                    fail();
                    return;
                }
                // user has been saved, the ID should be filled now
                assertNotNull(person.getField(mapper.ID));
                testComplete();
            }
        });
    }

    @Test
    public void loadFromBackend() {
        // using the java inner class way
        final Person person = mapper.new Person();
        // set some fields
        person.putString("name", "Paulo");
        person.putNumber("age", 33);

        person.save(new Handler<Boolean>() {
            @Override
            public void handle(Boolean saved) {
                if (!saved) {
                    fail();
                    return;
                }
                // user has been saved, the ID should be filled now
                assertNotNull(person.getField(mapper.ID));
                String id = person.getId();

                // load from the DB
                mapper.findOne(id, new AsyncResultHandler<Person>() {
                    @Override
                    public void handle(AsyncResult<Person> findById) {
                        if (findById.failed()) {
                            fail(findById.cause().getMessage());
                            return;
                        }

                        Person person1 = findById.result();

                        assertEquals("Paulo", person1.getString("name"));
                        assertEquals(33, person1.getNumber("age"));
                        testComplete();
                    }
                });
            }
        });
    }

    @Override
    public void start() {
        initialize();
        container.deployModule("io.vertx~mod-redis~1.1.2", config, new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
                if (asyncResult.failed()) {
                    container.logger().error(asyncResult.cause());
                }
                assertTrue(asyncResult.succeeded());
                assertNotNull("deploymentID should not be null", asyncResult.result());

                mapper = new RedisPersons(getVertx().eventBus(), config.getString("address"));
                startTests();
            }
        });
    }
}
