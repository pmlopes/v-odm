package io.vertx.orm;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Person {

    private Long id;
    private String name;
    private int age;

    public String sayHello() {
        return "Hello " + getName();
    }

    public JsonObject toJson() {
      final JsonObject json = new JsonObject();
      PersonConverter.toJson(this, json);
      return json;
    }

    public Person() {}
    public Person(Person other) {
        // TODO: implement copy constructor, not really needed for examples and tests
    }

    public Person(JsonObject json) {
        PersonConverter.fromJson(json, this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
