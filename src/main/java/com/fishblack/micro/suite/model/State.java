package com.fishblack.micro.suite.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static com.fishblack.micro.suite.rabbitmq.utils.EnumUtils.getEnumFromString;


/**
 * Denotes if a notification is Read or Unread.
 */
public enum State {
    READ,
    UNREAD;

    @JsonCreator
    public static State fromString(String value) {
        return getEnumFromString(State.class, value);
    }

    @JsonValue
    public String toJsonValue() {
        return name().toUpperCase();
    }
}