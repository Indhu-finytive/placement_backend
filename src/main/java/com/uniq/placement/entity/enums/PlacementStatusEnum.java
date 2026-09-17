package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PlacementStatusEnum {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String value;

    PlacementStatusEnum(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static PlacementStatusEnum fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String clean = value.trim();
        for (PlacementStatusEnum s : values()) {
            if (s.name().equalsIgnoreCase(clean) || s.value.equalsIgnoreCase(clean)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown PlacementStatus: " + value);
    }
}
