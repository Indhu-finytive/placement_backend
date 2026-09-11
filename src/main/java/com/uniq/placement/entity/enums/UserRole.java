package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    ADMIN("Admin"),
    COLLECTION_USER("Collection User"),
    SHARE_PARTNER("Share Partner"),
    CUSTOM_USER("Custom User");

    private final String value;

    UserRole(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static UserRole fromValue(String value) {
        for (UserRole r : values()) {
            if (r.value.equalsIgnoreCase(value)) return r;
        }
        throw new IllegalArgumentException("Unknown UserRole: " + value);
    }
}
