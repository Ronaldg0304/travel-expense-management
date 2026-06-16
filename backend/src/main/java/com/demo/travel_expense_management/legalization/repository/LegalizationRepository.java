package com.demo.travel_expense_management.legalization.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.travel_expense_management.legalization.entity.Legalization;

public interface LegalizationRepository extends JpaRepository<Legalization, Long> {

    Optional<Legalization> findByTravelRequestId(Long travelRequestId);

    boolean existsByTravelRequestId(Long travelRequestId);
}
