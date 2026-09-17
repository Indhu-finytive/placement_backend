package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LedgerType {
    COMPANY("Company"),
    TEAM("Team");

    private final String value;

    LedgerType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static LedgerType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String clean = value.trim();
        for (LedgerType l : values()) {
            if (l.name().equalsIgnoreCase(clean) || l.value.equalsIgnoreCase(clean)) {
                return l;
            }
        }
        throw new IllegalArgumentException("Unknown LedgerType: " + value);
    }
}
