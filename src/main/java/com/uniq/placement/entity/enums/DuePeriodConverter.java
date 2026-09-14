package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class DuePeriodConverter implements AttributeConverter<DuePeriod, String> {
    @Override
    public String convertToDatabaseColumn(DuePeriod attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public DuePeriod convertToEntityAttribute(String dbData) {
        return dbData == null ? null : DuePeriod.fromValue(dbData);
    }
}
