package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PlacementStatusEnumConverter implements AttributeConverter<PlacementStatusEnum, String> {
    @Override
    public String convertToDatabaseColumn(PlacementStatusEnum attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public PlacementStatusEnum convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PlacementStatusEnum.fromValue(dbData);
    }
}
