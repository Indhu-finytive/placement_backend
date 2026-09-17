package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentType {
    OFFER_LETTER("OFFER_LETTER"),
    PAYMENT_RECEIPT("PAYMENT_RECEIPT"),
    OTHER("OTHER");

    private final String value;

    DocumentType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static DocumentType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String clean = value.trim();
        for (DocumentType d : values()) {
            if (d.name().equalsIgnoreCase(clean) || d.value.equalsIgnoreCase(clean)) {
                return d;
            }
        }
        String normalized = clean.replace(" ", "_");
        for (DocumentType d : values()) {
            if (d.name().equalsIgnoreCase(normalized)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Unknown DocumentType: " + value);
    }
}
