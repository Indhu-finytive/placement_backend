package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DuePeriod {
    DAYS_30("30 Days"),
    DAYS_45("45 Days"),
    DAYS_60("60 Days"),
    DAYS_90("90 Days"),
    CUSTOM("Custom");

    private final String value;

    DuePeriod(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static DuePeriod fromValue(String value) {
        for (DuePeriod d : values()) {
            if (d.value.equalsIgnoreCase(value)) return d;
        }
        throw new IllegalArgumentException("Unknown DuePeriod: " + value);
    }

    public int toDays() {
        return switch (this) {
            case DAYS_30 -> 30;
            case DAYS_45 -> 45;
            case DAYS_60 -> 60;
            case DAYS_90 -> 90;
            case CUSTOM -> 0;
        };
    }
}
