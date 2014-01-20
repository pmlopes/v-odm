package com.jetdrone.odm

import com.jetdrone.odm.backend.GPostgreSQLMapper
import org.vertx.groovy.core.eventbus.EventBus

class GPostgreSQLPersons extends GPostgreSQLMapper<Person> {


    public GPostgreSQLPersons(EventBus eventBus, String address) {
        super(eventBus, address, "psqlusers");
    }

    @Override
    protected Person newRecord(Map json) {
        if (json == null) {
            return null;
        }

        return new Person(this, json);
    }
}
