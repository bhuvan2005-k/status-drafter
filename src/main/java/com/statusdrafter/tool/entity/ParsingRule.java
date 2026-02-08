package com.statusdrafter.tool.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parsing_rules")
@Data
@NoArgsConstructor
public class ParsingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(unique = true, nullable = false)
    private String keyword;

    private Integer priority;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public enum Category {
        YESTERDAY, TODAY, BLOCKERS
    }
}
