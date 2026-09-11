package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    private final String value;

    Gender(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static Gender fromValue(String value) {
        for (Gender g : values()) {
            if (g.value.equalsIgnoreCase(value)) return g;
        }
        throw new IllegalArgumentException("Unknown Gender: " + value);
    }
}
