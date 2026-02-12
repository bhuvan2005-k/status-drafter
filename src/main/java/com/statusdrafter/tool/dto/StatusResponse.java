package com.statusdrafter.tool.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class StatusResponse {
    private Long id;
    private String userName;
    private LocalDate statusDate;
    private ParsedDetails formattedReport;
    private Double confidenceScore;

    @Data
    @Builder
    public static class ParsedDetails {
        private List<String> yesterday;
        private List<String> today;
        private List<String> blockers;
        private Double hoursSpent;
    }
}
