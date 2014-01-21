package com.jetdrone.odm;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import com.jetdrone.odm.MongoPersons.Person;

import java.util.List;

import static org.vertx.testtools.VertxAssert.*;

public class MongoDBTest extends TestVerticle {

    final JsonObject config = new JsonObject()
            .putString("address", "mongo.db")
            .putString("db_name", "db");

    private MongoPersons mapper;

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
                mapper.findById(id, new AsyncResultHandler<Person>() {
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

    @Test
    public void saveAndUpdate() {
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

                person.putString("name", "Paulo Lopes");

                person.update(new Handler<Boolean>() {
                    @Override
                    public void handle(Boolean updated) {
                        if (!updated) {
                            fail();
                            return;
                        }

                        // load from the DB
                        mapper.findById(person.getId(), new AsyncResultHandler<Person>() {
                            @Override
                            public void handle(AsyncResult<Person> findById) {
                                if (findById.failed()) {
                                    fail(findById.cause().getMessage());
                                    return;
                                }

                                Person person1 = findById.result();

                                assertEquals("Paulo Lopes", person1.getString("name"));
                                assertEquals(33, person1.getNumber("age"));
                                testComplete();
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void saveAndRemove() {
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

                person.remove(new Handler<Boolean>() {
                    @Override
                    public void handle(Boolean updated) {
                        if (!updated) {
                            fail();
                            return;
                        }

                        // load from the DB
                        mapper.findById(person.getId(), new AsyncResultHandler<Person>() {
                            @Override
                            public void handle(AsyncResult<Person> findById) {
                                if (findById.failed()) {
                                    fail(findById.cause().getMessage());
                                    return;
                                }

                                Person person1 = findById.result();

                                assertNull(person1);
                                testComplete();
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void dropCollection() {
        mapper.truncate(new AsyncResultHandler<Void>() {
            @Override
            public void handle(AsyncResult<Void> drop) {
                if (drop.failed()) {
                    fail(drop.cause().getMessage());
                    return;
                }

                testComplete();
            }
        });
    }

    @Test
    public void findAll() {
        mapper.truncate(new AsyncResultHandler<Void>() {
            @Override
            public void handle(AsyncResult<Void> drop) {
                if (drop.failed()) {
                    fail(drop.cause().getMessage());
                    return;
                }

                final Person person = mapper.new Person();
                person.save(new Handler<Boolean>() {
                    @Override
                    public void handle(Boolean saved) {
                        if (!saved) {
                            fail();
                            return;
                        }

                        // find all
                        mapper.findAll(new AsyncResultHandler<List<Person>>() {
                            @Override
                            public void handle(AsyncResult<List<Person>> findAll) {
                                if (findAll.failed()) {
                                    fail(findAll.cause().getMessage());
                                    return;
                                }

                                List<Person> persons = findAll.result();

                                assertNotNull(persons);
                                assertTrue(persons.size() == 1);

                                assertEquals(person, persons.get(0));
                                testComplete();
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void find() {
        mapper.truncate(new AsyncResultHandler<Void>() {
            @Override
            public void handle(AsyncResult<Void> drop) {
                if (drop.failed()) {
                    fail(drop.cause().getMessage());
                    return;
                }

                final Person person1 = mapper.new Person();
                person1.save(new Handler<Boolean>() {
                    @Override
                    public void handle(Boolean saved) {
                        if (!saved) {
                            fail();
                            return;
                        }

                        final Person person2 = mapper.new Person();
                        person2.save(new Handler<Boolean>() {
                            @Override
                            public void handle(Boolean saved) {
                                if (!saved) {
                                    fail();
                                    return;
                                }

                                // find using the person1 id
                                mapper.find(new JsonObject().putString(mapper.ID, person1.getId()), new AsyncResultHandler<List<Person>>() {
                                    @Override
                                    public void handle (AsyncResult<List<Person>> find) {
                                        if (find.failed()) {
                                            fail(find.cause().getMessage());
                                            return;
                                        }

                                        List<Person> persons = find.result();

                                        assertNotNull(persons);
                                        assertTrue(persons.size() == 1);

                                        assertEquals(person1, persons.get(0));
                                        testComplete();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void findOne() {
        mapper.truncate(new AsyncResultHandler<Void>() {
            @Override
            public void handle(AsyncResult<Void> drop) {
                if (drop.failed()) {
                    fail(drop.cause().getMessage());
                    return;
                }

                final Person person1 = mapper.new Person();
                person1.save(new Handler<Boolean>() {
                    @Override
                    public void handle(Boolean saved) {
                        if (!saved) {
                            fail();
                            return;
                        }

                        final Person person2 = mapper.new Person();
                        person2.save(new Handler<Boolean>() {
                            @Override
                            public void handle(Boolean saved) {
                                if (!saved) {
                                    fail();
                                    return;
                                }

                                // find using the person1 id
                                mapper.findOne(new JsonObject().putString(mapper.ID, person1.getId()), new AsyncResultHandler<Person>() {
                                    @Override
                                    public void handle(AsyncResult<Person> findOne) {
                                        if (findOne.failed()) {
                                            fail(findOne.cause().getMessage());
                                            return;
                                        }

                                        Person p = findOne.result();

                                        assertNotNull(p);

                                        assertEquals(person1, p);
                                        testComplete();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void count() {
        mapper.truncate(new AsyncResultHandler<Void>() {
            @Override
            public void handle(AsyncResult<Void> drop) {
                if (drop.failed()) {
                    fail(drop.cause().getMessage());
                    return;
                }

                final Person person1 = mapper.new Person();
                person1.save(new Handler<Boolean>() {
                    @Override
                    public void handle(Boolean saved) {
                        if (!saved) {
                            fail();
                            return;
                        }

                        final Person person2 = mapper.new Person();
                        person2.save(new Handler<Boolean>() {
                            @Override
                            public void handle(Boolean saved) {
                                if (!saved) {
                                    fail();
                                    return;
                                }

                                // find using the person1 id
                                mapper.count(new AsyncResultHandler < Number > () {
                                    @Override
                                    public void handle (AsyncResult<Number> count) {
                                        if (count.failed()) {
                                            fail(count.cause().getMessage());
                                            return;
                                        }

                                        Number c = count.result();

                                        assertNotNull(c);

                                        assertEquals(2, c.intValue());
                                        testComplete();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void start() {
        initialize();
        container.deployModule("io.vertx~mod-mongo-persistor~2.1.0", config, new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
                if (asyncResult.failed()) {
                    container.logger().error(asyncResult.cause());
                }
                assertTrue(asyncResult.succeeded());
                assertNotNull("deploymentID should not be null", asyncResult.result());

                mapper = new MongoPersons(getVertx().eventBus(), config.getString("address"));
                startTests();
            }
        });
    }
}
