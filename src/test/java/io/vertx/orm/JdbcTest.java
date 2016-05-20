package io.vertx.orm;

import io.vertx.core.Vertx;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.vertx.core.json.JsonObject;

import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class JdbcTest {

  private Vertx vertx;
  private SQLMapper mapper;

  @Before
  public void before(TestContext test) {
    final Async async = test.async();

    vertx = Vertx.vertx();

    final JDBCClient jdbc = JDBCClient.createShared(vertx, new JsonObject()
        .put("url", "jdbc:h2:mem:test")
        .put("driver_class", "org.h2.Driver"));

    // setup some test data
    jdbc.getConnection(res -> {
      if (res.failed()) {
        test.fail(res.cause());
        async.complete();
      } else {
        res.result().execute(
            "CREATE TABLE IF NOT EXISTS users ( " +
                "id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL, " +
                "name varchar(255), " +
                "age integer, " +
                "CONSTRAINT person_id PRIMARY KEY ( id ) )", res2 -> {
              if (res2.failed()) {
                test.fail(res.cause());
                async.complete();
              } else {
                mapper = SQLMapper.create(jdbc, "users");
                async.complete();
              }
            });
      }
    });
  }

  @After
  public void after(TestContext test) {
    vertx.close(test.asyncAssertSuccess());
  }

  @Test
  public void saveNewInstance(TestContext test) {
    final Async async = test.async();

    // using the java inner class way
    final Person person = new Person();
    test.assertNotNull(person);

    // set some fields
    person.setName("Paulo");
    person.setAge(33);
    // there is no id
    test.assertNull(person.getId());
    // save
    mapper.save(person.toJson(), save -> {
      test.assertTrue(save.succeeded());
      async.complete();
    });
  }

  @Test
  public void loadFromBackend(TestContext test) {
    final Async async = test.async();

    // using the java inner class way
    final Person person = new Person();
    // set some fields
    person.setName("Paulo");
    person.setAge(33);

    mapper.save(person.toJson(), save -> {
      test.assertTrue(save.succeeded());
      // user has been saved, the ID should be filled now
      person.setId(save.result());

      // load from the DB
      mapper.find("WHERE id = " + person.getId(), find -> {
        test.assertTrue(find.succeeded());
        test.assertEquals(1, find.result().size());

        Person person1 = new Person(find.result().get(0));

        test.assertEquals("Paulo", person1.getName());
        test.assertEquals(33, person1.getAge());
        async.complete();
      });
    });
  }

  @Test
  public void saveAndUpdate(TestContext test) {
    final Async async = test.async();

    final Person person = new Person();
    // set some fields
    person.setName("Paulo");
    person.setAge(33);

    mapper.save(person.toJson(), save -> {
      test.assertTrue(save.succeeded());

      person.setName("Paulo Lopes");

      mapper.update("WHERE id = "  + save.result(), person.toJson(), update -> {
        test.assertTrue(update.succeeded());
        test.assertEquals(1, update.result());

        // load from the DB
        mapper.find("WHERE id = " + save.result(), find -> {
          test.assertTrue(find.succeeded());
          test.assertEquals(1, find.result().size());

          Person person1 = new Person(find.result().get(0));

          test.assertEquals("Paulo Lopes", person1.getName());
          test.assertEquals(33, person1.getAge());
          async.complete();
        });
      });
    });
  }

  @Test
  public void saveAndRemove(TestContext test) {
    final Async async = test.async();

    final Person person = new Person();
    // set some fields
    person.setName("Paulo");
    person.setAge(33);

    mapper.save(person.toJson(), save -> {
      test.assertTrue(save.succeeded());

      mapper.remove("WHERE id = " + save.result(), remove -> {
        test.assertTrue(remove.succeeded());

        // load from the DB
        mapper.find("WHERE id = " + save.result(), find -> {
          test.assertTrue(find.succeeded());
          test.assertEquals(0, find.result().size());
          async.complete();
        });
      });
    });
  }

  @Test
  public void dropCollection(TestContext test) {
    final Async async = test.async();

    mapper.truncate(truncate -> {
      test.assertTrue(truncate.succeeded());
      async.complete();
    });
  }

  @Test
  public void find(TestContext test) {
    final Async async = test.async();

    mapper.truncate(truncate -> {
      test.assertTrue(truncate.succeeded());

      final Person person1 = new Person();
      person1.setName("p1");
      person1.setAge(1);

      mapper.save(person1.toJson(), save1 -> {
        test.assertTrue(save1.succeeded());

        final Person person2 = new Person();
        person2.setName("p2");
        person2.setAge(2);

        mapper.save(person2.toJson(), save2 -> {
          test.assertTrue(save2.succeeded());

          // find using the person1 id
          mapper.find("WHERE id = " + save1.result(), find -> {
            test.assertTrue(find.succeeded());
            test.assertEquals(1, find.result().size());
            // add the id to the person1 object to the test should be equal
            person1.setId(save1.result());
            test.assertEquals(person1.toJson(), find.result().get(0));
            async.complete();
          });
        });
      });
    });
  }

  @Test
  public void count(TestContext test) {
    final Async async = test.async();

    mapper.truncate(truncate -> {
      test.assertTrue(truncate.succeeded());

      final Person person1 = new Person();
      person1.setName("p1");
      person1.setAge(1);

      mapper.save(person1.toJson(), save1 -> {
        test.assertTrue(save1.succeeded());

        final Person person2 = new Person();
        person2.setName("p2");
        person2.setAge(2);

        mapper.save(person2.toJson(), save2 -> {
          test.assertTrue(save2.succeeded());

          // find using the person1 id
          mapper.countAll(count -> {
            test.assertTrue(count.succeeded());
            test.assertEquals(2L, count.result());
            async.complete();
          });
        });
      });
    });
  }
}