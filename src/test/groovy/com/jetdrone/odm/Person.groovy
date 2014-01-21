package com.jetdrone.odm

import com.jetdrone.odm.Mapper

class Person extends GRecord<Number> {
    public Person(Mapper mapper) {
        super(mapper);
    }

    public Person(Mapper mapper, Map<String, Object> map) {
        super(mapper, map);
    }

    public String sayHello() {
        return "Hello " + getString("name");
    }
}
