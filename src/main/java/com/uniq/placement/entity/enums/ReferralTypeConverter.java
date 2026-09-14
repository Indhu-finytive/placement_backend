package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ReferralTypeConverter implements AttributeConverter<ReferralType, String> {
    @Override
    public String convertToDatabaseColumn(ReferralType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ReferralType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ReferralType.fromValue(dbData);
    }
}
