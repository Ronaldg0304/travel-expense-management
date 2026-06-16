package com.demo.travel_expense_management.refund.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.travel_expense_management.refund.entity.Refund;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    Optional<Refund> findByLegalizationId(Long legalizationId);

    boolean existsByLegalizationId(Long legalizationId);
}
