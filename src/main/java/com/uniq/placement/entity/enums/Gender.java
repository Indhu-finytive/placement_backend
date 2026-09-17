package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    private final String value;

    Gender(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static Gender fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String clean = value.trim();
        for (Gender g : values()) {
            if (g.name().equalsIgnoreCase(clean) || g.value.equalsIgnoreCase(clean)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Unknown Gender: " + value);
    }
}
