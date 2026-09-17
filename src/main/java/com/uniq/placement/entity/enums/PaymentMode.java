package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public static PaymentMode fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String clean = value.trim();
        for (PaymentMode m : values()) {
            if (m.name().equalsIgnoreCase(clean) || m.value.equalsIgnoreCase(clean)) {
                return m;
            }
        }
        String normalizedWithUnderscore = clean.replace(" ", "_");
        for (PaymentMode m : values()) {
            if (m.name().equalsIgnoreCase(normalizedWithUnderscore)) {
                return m;
            }
        }
        String normalizedWithSpace = clean.replace("_", " ");
        for (PaymentMode m : values()) {
            if (m.value.equalsIgnoreCase(normalizedWithSpace)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentMode: '" + value + "'. Accepted values: CASH, UPI, QR, BANK_TRANSFER, OTHER");
    }
}
