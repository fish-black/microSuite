package com.fishblack.micro.suite.rabbitmq.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Serialize user to be lowercase
 */
public class UserSerializer extends JsonSerializer<String> {
    public void serialize(String value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {
            jgen.writeString(NotificationUtils.handleUserCase(value));
    }
}