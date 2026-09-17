package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {
    DOCUMENT_FEE("Document Fee"),
    PLACEMENT_INSTALLMENT("Placement Installment"),
    REFUND("Refund"),
    ADJUSTMENT("Adjustment");

    private final String value;

    PaymentType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static PaymentType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String clean = value.trim();
        for (PaymentType t : values()) {
            if (t.name().equalsIgnoreCase(clean) || t.value.equalsIgnoreCase(clean)) {
                return t;
            }
        }
        String normalizedWithUnderscore = clean.replace(" ", "_");
        for (PaymentType t : values()) {
            if (t.name().equalsIgnoreCase(normalizedWithUnderscore)) {
                return t;
            }
        }
        String normalizedWithSpace = clean.replace("_", " ");
        for (PaymentType t : values()) {
            if (t.value.equalsIgnoreCase(normalizedWithSpace)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentType: '" + value + "'. Accepted values: DOCUMENT_FEE, PLACEMENT_INSTALLMENT, REFUND, ADJUSTMENT");
    }
}
