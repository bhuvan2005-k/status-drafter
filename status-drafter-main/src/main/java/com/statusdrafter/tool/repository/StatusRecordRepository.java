package com.statusdrafter.tool.repository;

import com.statusdrafter.tool.entity.StatusRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface StatusRecordRepository extends JpaRepository<StatusRecord, Long> {
    List<StatusRecord> findAllByStatusDate(LocalDate date);
    List<StatusRecord> findAllByUserIdAndStatusDateBetween(Long userId, LocalDate start, LocalDate end);
}
