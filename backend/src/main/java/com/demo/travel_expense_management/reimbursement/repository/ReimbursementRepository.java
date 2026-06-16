package com.demo.travel_expense_management.reimbursement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.travel_expense_management.reimbursement.entity.Reimbursement;

public interface ReimbursementRepository extends JpaRepository<Reimbursement, Long> {

    Optional<Reimbursement> findByLegalizationId(Long legalizationId);

    boolean existsByLegalizationId(Long legalizationId);
}
