package com.jetdrone.odm;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.redis.RedisClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.vertx.core.json.JsonObject;
import org.junit.runner.RunWith;

import com.jetdrone.odm.RedisPersons.Person;

@RunWith(VertxUnitRunner.class)
public class RedisTest {

  final JsonObject config = new JsonObject()
      .put("address", "redis.db");

  private Vertx vertx;
  private RedisPersons mapper;

  @Before
  public void before(TestContext test) {
    vertx = Vertx.vertx();
    mapper = new RedisPersons(RedisClient.create(vertx, config));
  }

  @After
  public void after(TestContext test) {
    vertx.close(test.asyncAssertSuccess());
  }

  @Test
  public void createSingleInstance(TestContext test) {
    // using the java inner class way
    Person person = mapper.new Person();
    test.assertNotNull(person);
    test.async().complete();
  }

  @Test
  public void saveNewInstance(TestContext test) {
    final Async async = test.async();

    // using the java inner class way
    final Person person = mapper.new Person();
    test.assertNotNull(person);

    // set some fields
    person.put("name", "Paulo");
    person.put("age", "33");
    // there is no _id
    test.assertNull(person.getValue(mapper.ID));
    // save
    person.save(saved -> {
      if (!saved) {
        test.fail();
        return;
      }
      // user has been saved, the ID should be filled now
      test.assertNotNull(person.getValue(mapper.ID));
      async.complete();
    });
  }

  @Test
  public void loadFromBackend(TestContext test) {
    final Async async = test.async();

    // using the java inner class way
    final Person person = mapper.new Person();
    // set some fields
    person.put("name", "Paulo");
    person.put("age", "33");

    person.save(saved -> {
      if (!saved) {
        test.fail();
        return;
      }
      // user has been saved, the ID should be filled now
      test.assertNotNull(person.getValue(mapper.ID));
      String id = person.getId();

      // load from the DB
      mapper.findOne(id, findById -> {
        if (findById.failed()) {
          test.fail(findById.cause());
          return;
        }

        Person person1 = findById.result();

        test.assertEquals("Paulo", person1.getString("name"));
        test.assertEquals("33", person1.getString("age"));
        async.complete();
      });
    });
  }
}
