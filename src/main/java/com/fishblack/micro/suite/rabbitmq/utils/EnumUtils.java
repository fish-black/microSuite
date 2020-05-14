package com.fishblack.micro.suite.rabbitmq.utils;

public class EnumUtils {
    public static <T extends Enum<T>> T getEnumFromString(Class<T> enumClass, String value) {
        if (enumClass == null) {
            throw new IllegalArgumentException("Enum class can not be null");
        }

        // Check for case insensitive match
        for(Enum<?> enumValue : enumClass.getEnumConstants()) {
            if (enumValue.toString().equalsIgnoreCase(value)) {
                return (T) enumValue;
            }
        }
        
        // Otherwise create an error message that give valid values for the enum
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append(value);
        errorMsg.append(" is an invalid value. Supported are: ");
        boolean first = true;
        for (Enum<?> enumValue : enumClass.getEnumConstants()) {
            errorMsg.append(first ? "" : ", ").append(enumValue);
            first = false;
        }
        throw new IllegalArgumentException(errorMsg.toString());
    }
}
