package com.jetdrone.odm

import com.jetdrone.odm.backend.GJdbcMapper
import org.vertx.groovy.core.eventbus.EventBus

class GJdbcPersons extends GJdbcMapper<Person> {

    GJdbcPersons(EventBus eventBus, String address) {
        super(eventBus, address, "users")
    }

    @Override
    protected Person newRecord(Map json) {
        if (json == null) {
            return null
        }

        new Person(this, json)
    }
}
