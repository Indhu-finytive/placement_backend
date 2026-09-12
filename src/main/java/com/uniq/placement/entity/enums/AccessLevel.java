package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccessLevel {
    FULL_ACCESS("Full access"),
    COLLECTION_ENTRY("Collection Entry"),
    SHARE_VIEW_ONLY("Share View Only"),
    ENTRY_VIEW("Entry + View"),
    VIEW_ONLY("View Only");

    private final String value;

    AccessLevel(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static AccessLevel fromValue(String value) {
        for (AccessLevel a : values()) {
            if (a.value.equalsIgnoreCase(value)) return a;
            if (a.name().equalsIgnoreCase(value)) return a;
        }
        throw new IllegalArgumentException("Unknown AccessLevel: " + value);
    }
}
