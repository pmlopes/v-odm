package com.jetdrone.odm

class Person extends GRecord<Number> {
    public Person(GPostgreSQLPersons mapper) {
        super(mapper);
    }

    public Person(GPostgreSQLPersons mapper, Map<String, Object> map) {
        super(mapper, map);
    }

    public String sayHello() {
        return "Hello " + getString("name");
    }
}
