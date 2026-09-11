package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActiveStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String value;

    ActiveStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ActiveStatus fromValue(String value) {
        for (ActiveStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
            if (s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown ActiveStatus: " + value);
    }
}
