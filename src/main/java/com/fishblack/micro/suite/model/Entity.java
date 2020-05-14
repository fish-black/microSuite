package com.fishblack.micro.suite.model;

import com.fishblack.micro.suite.model.validation.ValidEnum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.dropwizard.jackson.Jackson;

import javax.validation.constraints.NotNull;

import java.io.IOException;

import java.util.Map;

import static com.fishblack.micro.suite.rabbitmq.utils.EnumUtils.getEnumFromString;

/**
 * Entity object within a notification object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entity {

    public static String HUMAN_READABLE_ID = "humanReadableId";
    public static String STATE = "state";
    public static String SUMMARY = "summary";
    public static String NAME = "name";
    public static String MESSAGE = "message";
    public static String REQUEST_TYPE= "requestType";

    @NotNull
    @ValidEnum(enumClass = EntityType.class)
    private String type;

    private String uri;

    @NotNull
    private Map<String, String> fields;

    public EntityType getType() {
        return getEnumFromString(EntityType.class, type);
    }

    public void setType(EntityType type) {
            this.type = type != null ? type.toJsonValue() : null;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String toJsonString() throws IOException {
        ObjectMapper mapper = Jackson.newObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper.writeValueAsString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (type != entity.type) return false;
        if (uri != null ? !uri.equals(entity.uri) : entity.uri != null) return false;
        return !(fields != null ? !fields.equals(entity.fields) : entity.fields != null);
    }
}
