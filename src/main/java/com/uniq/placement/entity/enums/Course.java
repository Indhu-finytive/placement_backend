package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Course {
    JAVA("Java"),
    PYTHON("Python"),
    PRODUCTION_SUPPORT("Production Support"),
    DEVOPS("Devops");

    private final String value;

    Course(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static Course fromValue(String value) {
        for (Course c : values()) {
            if (c.value.equalsIgnoreCase(value)) return c;
        }
        throw new IllegalArgumentException("Unknown Course: " + value);
    }
}
