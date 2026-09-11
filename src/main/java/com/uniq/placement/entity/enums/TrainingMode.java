package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TrainingMode {
    ONLINE("Online"),
    OFFLINE("Offline"),
    HYBRID("Hybrid");

    private final String value;

    TrainingMode(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static TrainingMode fromValue(String value) {
        for (TrainingMode m : values()) {
            if (m.value.equalsIgnoreCase(value)) return m;
        }
        throw new IllegalArgumentException("Unknown TrainingMode: " + value);
    }
}
