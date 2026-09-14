package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class SettlementDirectionConverter implements AttributeConverter<SettlementDirection, String> {
    @Override
    public String convertToDatabaseColumn(SettlementDirection attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public SettlementDirection convertToEntityAttribute(String dbData) {
        return dbData == null ? null : SettlementDirection.fromValue(dbData);
    }
}
