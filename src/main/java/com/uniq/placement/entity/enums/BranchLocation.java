package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BranchLocation {
    CHENNAI("Chennai"),
    BANGALORE("Bangalore"),
    COIMBATORE("Coimbatore"),
    MADURAI("Madurai"),
    PONDICHERRY("Pondicherry"),
    SALEM("Salem"),
    TIRUNELVELI("Tirunelveli"),
    TRICHY("Trichy");

    private final String value;

    BranchLocation(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BranchLocation fromValue(String value) {
        for (BranchLocation location : values()) {
            if (location.value.equalsIgnoreCase(value)) return location;
            if (location.name().equalsIgnoreCase(value)) return location;
        }
        throw new IllegalArgumentException("Unknown branch location: " + value);
    }
}
