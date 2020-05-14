package com.fishblack.micro.suite.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static com.fishblack.micro.suite.rabbitmq.utils.EnumUtils.getEnumFromString;

/**
 * Denotes entity type for a specific entity within a notification.
 */
public enum EntityType {
    ANNOUNCEMENT,
    ORDER,
    REQUEST,
    APPROVAL,
    COMMENT,
    SUPPORT,
    EXTERNAL,
    COMMENT_REQUEST,
    COMMENT_SUPPORT,
    COMMENT_EXTERNAL;

    @JsonCreator
    public static EntityType fromString(String value) {
        return getEnumFromString(EntityType.class, value);
    }

    @JsonValue
    public String toJsonValue() {
        return name().toUpperCase();
    }
}