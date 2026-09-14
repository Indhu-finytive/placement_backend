package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class EligibilityConverter implements AttributeConverter<Eligibility, String> {
    @Override
    public String convertToDatabaseColumn(Eligibility attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Eligibility convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Eligibility.fromValue(dbData);
    }
}
