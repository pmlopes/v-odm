package io.vertx.orm;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicLong;

@RunWith(VertxUnitRunner.class)
public class MongoDBTest {

  private Vertx vertx;
  private NoSQLMapper mapper;

  private static final AtomicLong idx = new AtomicLong(0);

  @Before
  public void before(TestContext test) {
    final Async async = test.async();

    vertx = Vertx.vertx();

    mapper = NoSQLMapper.create(MongoClient.createShared(vertx, new JsonObject().put("db_name", "db")), "users");
    async.complete();
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
    // add some id
    person.setId(idx.incrementAndGet());

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
    // add some id
    person.setId(idx.incrementAndGet());

    mapper.save(person.toJson(), save -> {
      test.assertTrue(save.succeeded());

      // load from the DB
      mapper.find(new JsonObject().put("id", person.getId()), find -> {
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
    // add some id
    person.setId(idx.incrementAndGet());

    mapper.save(person.toJson(), save -> {
      test.assertTrue(save.succeeded());

      person.setName("Paulo Lopes");

      mapper.update(new JsonObject().put("id", person.getId()), person.toJson(), update -> {
        test.assertTrue(update.succeeded());

        // load from the DB
        mapper.find(new JsonObject().put("id", person.getId()), find -> {
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
    // add some id
    person.setId(idx.incrementAndGet());

    mapper.save(person.toJson(), save -> {
      test.assertTrue(save.succeeded());

      mapper.remove(new JsonObject().put("id", save.result()), remove -> {
        test.assertTrue(remove.succeeded());

        // load from the DB
        mapper.find(new JsonObject().put("id", save.result()), find -> {
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
      // add some id
      person1.setId(idx.incrementAndGet());

      mapper.save(person1.toJson(), save1 -> {
        test.assertTrue(save1.succeeded());

        final Person person2 = new Person();
        person2.setName("p2");
        person2.setAge(2);
        // add some id
        person2.setId(idx.incrementAndGet());

        mapper.save(person2.toJson(), save2 -> {
          test.assertTrue(save2.succeeded());

          // find using the person1 id
          mapper.find(new JsonObject().put("id", person1.getId()), find -> {
            test.assertTrue(find.succeeded());
            test.assertEquals(1, find.result().size());
            // need to remove _id form db so it matches
            find.result().get(0).remove("_id");
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
