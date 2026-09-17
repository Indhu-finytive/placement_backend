package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReferralType {
    DIRECT("Direct"),
    CONSULTANT("Consultant"),
    EMPLOYEE_REFERRAL("Employee Referral"),
    OTHER("Other");

    private final String value;

    ReferralType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ReferralType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String clean = value.trim();
        for (ReferralType r : values()) {
            if (r.name().equalsIgnoreCase(clean) || r.value.equalsIgnoreCase(clean)) {
                return r;
            }
        }
        String normalizedWithUnderscore = clean.replace(" ", "_");
        for (ReferralType r : values()) {
            if (r.name().equalsIgnoreCase(normalizedWithUnderscore)) {
                return r;
            }
        }
        String normalizedWithSpace = clean.replace("_", " ");
        for (ReferralType r : values()) {
            if (r.value.equalsIgnoreCase(normalizedWithSpace)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown ReferralType: " + value);
    }
}
