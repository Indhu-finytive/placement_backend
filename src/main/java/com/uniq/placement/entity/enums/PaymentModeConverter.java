package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PaymentModeConverter implements AttributeConverter<PaymentMode, String> {
    @Override
    public String convertToDatabaseColumn(PaymentMode attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public PaymentMode convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PaymentMode.fromValue(dbData);
    }
}
