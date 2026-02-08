package com.statusdrafter.tool.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "status_records")
@Data
@NoArgsConstructor
public class StatusRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "raw_content", columnDefinition = "TEXT", nullable = false)
    private String rawContent;

    @Column(name = "parsed_yesterday", columnDefinition = "TEXT")
    private String parsedYesterday;

    @Column(name = "parsed_today", columnDefinition = "TEXT")
    private String parsedToday;

    @Column(name = "parsed_blockers", columnDefinition = "TEXT")
    private String parsedBlockers;

    @Column(name = "total_hours_spent")
    private Double totalHoursSpent;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "status_date", nullable = false)
    private LocalDate statusDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
