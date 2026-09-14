package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TrainingModeConverter implements AttributeConverter<TrainingMode, String> {
    @Override
    public String convertToDatabaseColumn(TrainingMode attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public TrainingMode convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TrainingMode.fromValue(dbData);
    }
}
