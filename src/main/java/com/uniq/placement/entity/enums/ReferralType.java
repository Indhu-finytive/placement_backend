package com.uniq.placement.entity.enums;

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

    public static ReferralType fromValue(String value) {
        for (ReferralType r : values()) {
            if (r.value.equalsIgnoreCase(value)) return r;
        }
        throw new IllegalArgumentException("Unknown ReferralType: " + value);
    }
}
