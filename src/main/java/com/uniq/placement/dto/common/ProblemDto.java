package com.uniq.placement.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDto {
    @Builder.Default
    private String type = "about:blank";
    private int status;
    private String title;
    private String detail;
    private String traceId;
}
