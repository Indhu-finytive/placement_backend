package com.uniq.placement.entity.enums;

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

    public static PaymentType fromValue(String value) {
        for (PaymentType t : values()) {
            if (t.value.equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Unknown PaymentType: " + value);
    }
}
