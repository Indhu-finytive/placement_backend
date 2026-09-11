package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PlacementStatusEnum {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String value;

    PlacementStatusEnum(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static PlacementStatusEnum fromValue(String value) {
        for (PlacementStatusEnum s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown PlacementStatus: " + value);
    }
}
