package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentType {
    OFFER_LETTER("OFFER_LETTER"),
    PAYMENT_RECEIPT("PAYMENT_RECEIPT"),
    OTHER("OTHER");

    private final String value;

    DocumentType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }
}
