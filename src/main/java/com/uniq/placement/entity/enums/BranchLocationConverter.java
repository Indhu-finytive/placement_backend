package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class BranchLocationConverter implements AttributeConverter<BranchLocation, String> {
    @Override
    public String convertToDatabaseColumn(BranchLocation attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public BranchLocation convertToEntityAttribute(String dbData) {
        return dbData == null ? null : BranchLocation.fromValue(dbData);
    }
}
