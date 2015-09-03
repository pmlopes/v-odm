package com.jetdrone.odm;

import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import com.jetdrone.odm.MongoPersons.Person;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(VertxUnitRunner.class)
public class MongoDBTest {

  private Vertx vertx;
  private MongoPersons mapper;

  @Before
  public void before(TestContext test) {
    vertx = Vertx.vertx();
    mapper = new MongoPersons(MongoClient.createShared(vertx, new JsonObject().put("db_name", "db")));
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
    person.put("age", 33);
    // there is no _id
    test.assertNull(person.getValue(mapper.ID));
    // save
    person.save(saved -> {
      if (!saved) {
        test.fail();
        async.complete();
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
    person.put("age", 33);

    person.save(saved -> {
      if (!saved) {
        test.fail();
        async.complete();
        return;
      }
      // user has been saved, the ID should be filled now
      test.assertNotNull(person.getValue(mapper.ID));
      String id = person.getId();

      // load from the DB
      mapper.findById(id, findById -> {
        if (findById.failed()) {
          test.fail(findById.cause());
          async.complete();
          return;
        }

        Person person1 = findById.result();

        test.assertEquals("Paulo", person1.getString("name"));
        test.assertEquals(33, person1.getInteger("age"));
        async.complete();
      });
    });
  }

  @Test
  public void saveAndUpdate(TestContext test) {
    final Async async = test.async();
    // using the java inner class way
    final Person person = mapper.new Person();
    // set some fields
    person.put("name", "Paulo");
    person.put("age", 33);

    person.save(new Handler<Boolean>() {
      @Override
      public void handle(Boolean saved) {
        if (!saved) {
          test.fail();
          async.complete();
          return;
        }
        // user has been saved, the ID should be filled now
        test.assertNotNull(person.getValue(mapper.ID));

        person.put("name", "Paulo Lopes");

        person.update(updated -> {
          if (!updated) {
            test.fail();
            async.complete();
            return;
          }

          // load from the DB
          mapper.findById(person.getId(), findById -> {
            if (findById.failed()) {
              test.fail(findById.cause());
              async.complete();
              return;
            }

            Person person1 = findById.result();

            test.assertEquals("Paulo Lopes", person1.getString("name"));
            test.assertEquals(33, person1.getInteger("age"));
            async.complete();
          });
        });
      }
    });
  }

  @Test
  public void saveAndRemove(TestContext test) {
    final Async async = test.async();
    // using the java inner class way
    final Person person = mapper.new Person();
    // set some fields
    person.put("name", "Paulo");
    person.put("age", 33);

    person.save(saved -> {
      if (!saved) {
        test.fail();
        async.complete();
        return;
      }
      // user has been saved, the ID should be filled now
      test.assertNotNull(person.getValue(mapper.ID));

      person.remove(updated -> {
        if (!updated) {
          test.fail();
          async.complete();
          return;
        }

        // load from the DB
        mapper.findById(person.getId(), findById -> {
          if (findById.failed()) {
            test.fail(findById.cause());
            async.complete();
            return;
          }

          Person person1 = findById.result();

          test.assertNull(person1);
          async.complete();
        });
      });
    });
  }

  @Test
  public void dropCollection(TestContext test) {
    final Async async = test.async();

    mapper.truncate(drop -> {
      if (drop.failed()) {
        test.fail(drop.cause());
        async.complete();
        return;
      }

      async.complete();
    });
  }

  @Test
  public void findAll(TestContext test) {
    final Async async = test.async();

    mapper.truncate(drop -> {
      if (drop.failed()) {
        test.fail(drop.cause());
        async.complete();
        return;
      }

      final Person person = mapper.new Person();
      person.save(saved -> {
        if (!saved) {
          test.fail();
          async.complete();
          return;
        }

        // find all
        mapper.findAll(findAll -> {
          if (findAll.failed()) {
            test.fail(findAll.cause());
            async.complete();
            return;
          }

          List<Person> persons = findAll.result();

          test.assertNotNull(persons);
          test.assertTrue(persons.size() == 1);

          test.assertEquals(person, persons.get(0));
          async.complete();
        });
      });
    });
  }

  @Test
  public void find(TestContext test) {
    final Async async = test.async();

    mapper.truncate(drop -> {
      if (drop.failed()) {
        test.fail(drop.cause());
        async.complete();
        return;
      }

      final Person person1 = mapper.new Person();
      person1.save(saved -> {
        if (!saved) {
          test.fail();
          async.complete();
          return;
        }

        final Person person2 = mapper.new Person();
        person2.save(saved1 -> {
          if (!saved1) {
            test.fail();
            async.complete();
            return;
          }

          // find using the person1 id
          mapper.find(new JsonObject().put(mapper.ID, person1.getId()), find -> {
            if (find.failed()) {
              test.fail(find.cause());
              async.complete();
              return;
            }

            List<Person> persons = find.result();

            test.assertNotNull(persons);
            test.assertTrue(persons.size() == 1);

            test.assertEquals(person1, persons.get(0));
            async.complete();
          });
        });
      });
    });
  }

  @Test
  public void findOne(TestContext test) {
    final Async async = test.async();

    mapper.truncate(drop -> {
      if (drop.failed()) {
        test.fail(drop.cause());
        async.complete();
        return;
      }

      final Person person1 = mapper.new Person();
      person1.save(saved -> {
        if (!saved) {
          test.fail();
          async.complete();
          return;
        }

        final Person person2 = mapper.new Person();
        person2.save(saved1 -> {
          if (!saved1) {
            test.fail();
            async.complete();
            return;
          }

          // find using the person1 id
          mapper.findOne(new JsonObject().put(mapper.ID, person1.getId()), findOne -> {
            if (findOne.failed()) {
              test.fail(findOne.cause());
              async.complete();
              return;
            }

            Person p = findOne.result();

            test.assertNotNull(p);

            test.assertEquals(person1, p);
            async.complete();
          });
        });
      });
    });
  }

  @Test
  public void count(TestContext test) {
    final Async async = test.async();

    mapper.truncate(drop -> {
      if (drop.failed()) {
        test.fail(drop.cause());
        async.complete();
        return;
      }

      final Person person1 = mapper.new Person();
      person1.save(saved -> {
        if (!saved) {
          test.fail();
          async.complete();
          return;
        }

        final Person person2 = mapper.new Person();
        person2.save(saved1 -> {
          if (!saved1) {
            test.fail();
            async.complete();
            return;
          }

          // find using the person1 id
          mapper.count(count -> {
            if (count.failed()) {
              test.fail(count.cause());
              async.complete();
              return;
            }

            Number c = count.result();

            test.assertNotNull(c);

            test.assertEquals(2, c.intValue());
            async.complete();
          });
        });
      });
    });
  }
}
