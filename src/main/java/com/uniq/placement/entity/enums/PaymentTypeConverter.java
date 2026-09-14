package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PaymentTypeConverter implements AttributeConverter<PaymentType, String> {
    @Override
    public String convertToDatabaseColumn(PaymentType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public PaymentType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PaymentType.fromValue(dbData);
    }
}
