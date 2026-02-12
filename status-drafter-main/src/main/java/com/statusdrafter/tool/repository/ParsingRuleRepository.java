package com.statusdrafter.tool.repository;

import com.statusdrafter.tool.entity.ParsingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParsingRuleRepository extends JpaRepository<ParsingRule, Long> {
    List<ParsingRule> findAllByIsActiveTrue();
}
