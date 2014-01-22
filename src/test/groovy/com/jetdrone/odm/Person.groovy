package com.jetdrone.odm

import com.jetdrone.odm.Mapper

class Person extends GRecord<Number> {

    Person(Mapper mapper) {
        super(mapper)
    }

    Person(Mapper mapper, Map<String, Object> map) {
        super(mapper, map)
    }

    String sayHello() {
        "Hello ${getString("name")}"
    }
}
