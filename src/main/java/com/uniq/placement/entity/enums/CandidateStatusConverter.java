package com.uniq.placement.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CandidateStatusConverter implements AttributeConverter<CandidateStatus, String> {
    @Override
    public String convertToDatabaseColumn(CandidateStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public CandidateStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CandidateStatus.fromValue(dbData);
    }
}
