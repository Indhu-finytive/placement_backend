package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Eligibility {
    ELIGIBLE("Eligible"),
    HOLD("Hold"),
    NOT_ELIGIBLE("Not Eligible");

    private final String value;

    Eligibility(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static Eligibility fromValue(String value) {
        for (Eligibility e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
            if (e.name().equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Unknown Eligibility: " + value);
    }
}
