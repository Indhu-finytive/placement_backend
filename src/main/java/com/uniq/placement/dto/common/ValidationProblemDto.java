package com.uniq.placement.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationProblemDto {
    @Builder.Default
    private String type = "about:blank";
    private int status;
    private String title;
    private String detail;
    private String traceId;
    private Map<String, List<String>> errors;
}
