package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class LedgerTypeConverter implements AttributeConverter<LedgerType, String> {
    @Override
    public String convertToDatabaseColumn(LedgerType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public LedgerType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LedgerType.fromValue(dbData);
    }
}
