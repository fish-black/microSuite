package com.fishblack.micro.suite.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static com.fishblack.micro.suite.rabbitmq.utils.EnumUtils.getEnumFromString;

/**
 * Relations for related entities
 */
public enum Relation {
    CHILD,
    PARENT,
    REFERENCE;

    @JsonCreator
    public static Relation fromString(String value) {
        return getEnumFromString(Relation.class, value);
    }

    @JsonValue
    public String toJsonValue() {
        return name().toUpperCase();
    }
}
