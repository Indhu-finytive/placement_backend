package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ActiveStatusConverter implements AttributeConverter<ActiveStatus, String> {
    @Override
    public String convertToDatabaseColumn(ActiveStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ActiveStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ActiveStatus.fromValue(dbData);
    }
}
