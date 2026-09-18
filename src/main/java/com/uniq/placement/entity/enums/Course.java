package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Course {
    JAVA("Java"),
    PYTHON("Python"),
    PRODUCTION_SUPPORT("Production Support"),
    DEVOPS("Devops"),
    DOTNET("Dotnet");

    private final String value;

    Course(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static Course fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String clean = value.trim();
        for (Course c : values()) {
            if (c.name().equalsIgnoreCase(clean) || c.value.equalsIgnoreCase(clean)) {
                return c;
            }
        }
        String normalizedWithUnderscore = clean.replace(" ", "_");
        for (Course c : values()) {
            if (c.name().equalsIgnoreCase(normalizedWithUnderscore)) {
                return c;
            }
        }
        String normalizedWithSpace = clean.replace("_", " ");
        for (Course c : values()) {
            if (c.value.equalsIgnoreCase(normalizedWithSpace)) {
                return c;
            }
        }
        if (clean.equalsIgnoreCase(".net") || clean.equalsIgnoreCase("dot net") || clean.equalsIgnoreCase("dot_net")) {
            return DOTNET;
        }
        throw new IllegalArgumentException("Unknown Course: " + value);
    }
}
