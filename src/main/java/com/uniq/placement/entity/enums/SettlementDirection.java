package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SettlementDirection {
    PAID_TO_PARTNER("Paid to Partner"),
    RETURNED_TO_COMPANY("Returned to Company");

    private final String value;

    SettlementDirection(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static SettlementDirection fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String clean = value.trim();
        for (SettlementDirection d : values()) {
            if (d.name().equalsIgnoreCase(clean) || d.value.equalsIgnoreCase(clean)) {
                return d;
            }
        }
        String normalizedWithUnderscore = clean.replace(" ", "_");
        for (SettlementDirection d : values()) {
            if (d.name().equalsIgnoreCase(normalizedWithUnderscore)) {
                return d;
            }
        }
        String normalizedWithSpace = clean.replace("_", " ");
        for (SettlementDirection d : values()) {
            if (d.value.equalsIgnoreCase(normalizedWithSpace)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Unknown SettlementDirection: " + value);
    }
}
