package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SettlementDirection {
    PAID_TO_PARTNER("Paid to Partner"),
    RETURNED_TO_COMPANY("Returned to Company");

    private final String value;

    SettlementDirection(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static SettlementDirection fromValue(String value) {
        for (SettlementDirection d : values()) {
            if (d.value.equalsIgnoreCase(value)) return d;
        }
        throw new IllegalArgumentException("Unknown SettlementDirection: " + value);
    }
}
