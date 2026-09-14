package com.uniq.placement.dto.candidate;

import com.uniq.placement.dto.common.EnumOptionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationOptionsDto {
    private List<EnumOptionDto> candidateStatuses;
    private List<EnumOptionDto> eligibilities;
    private List<EnumOptionDto> courses;
}
