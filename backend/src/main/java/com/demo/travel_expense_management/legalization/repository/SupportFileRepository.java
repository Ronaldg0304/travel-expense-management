package com.demo.travel_expense_management.legalization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.travel_expense_management.legalization.entity.SupportFile;

public interface SupportFileRepository extends JpaRepository<SupportFile, Long> {

    List<SupportFile> findByLegalizationIdAndActiveTrue(Long legalizationId);

    List<SupportFile> findByLegalizationIdOrderByVersionNumberDesc(Long legalizationId);

    Optional<SupportFile> findTopByLegalizationIdOrderByVersionNumberDesc(Long legalizationId);
}
