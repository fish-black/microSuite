package com.fishblack.micro.suite.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static com.fishblack.micro.suite.rabbitmq.utils.EnumUtils.getEnumFromString;

/**
 * Denotes severity of a notification.
 */
public enum Severity {
    HIGH,
    MEDIUM,
    LOW;

    @JsonCreator
    public static Severity fromString(String value) {
        return getEnumFromString(Severity.class, value);
    }

    @JsonValue
    public String toJsonValue() {
        return name().toUpperCase();
    }
}