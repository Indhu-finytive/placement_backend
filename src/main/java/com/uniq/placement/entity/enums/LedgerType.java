package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LedgerType {
    COMPANY("Company"),
    TEAM("Team");

    private final String value;

    LedgerType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static LedgerType fromValue(String value) {
        for (LedgerType l : values()) {
            if (l.value.equalsIgnoreCase(value)) return l;
        }
        throw new IllegalArgumentException("Unknown LedgerType: " + value);
    }
}
