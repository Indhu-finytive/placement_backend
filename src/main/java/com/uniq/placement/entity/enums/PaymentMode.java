package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMode {
    UPI("UPI"),
    QR("QR"),
    BANK_TRANSFER("Bank Transfer"),
    CASH("Cash"),
    OTHER("Other");

    private final String value;

    PaymentMode(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static PaymentMode fromValue(String value) {
        for (PaymentMode m : values()) {
            if (m.value.equalsIgnoreCase(value)) return m;
        }
        throw new IllegalArgumentException("Unknown PaymentMode: " + value);
    }
}
