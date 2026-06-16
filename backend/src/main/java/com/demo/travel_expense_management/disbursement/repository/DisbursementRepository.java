package com.demo.travel_expense_management.disbursement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.travel_expense_management.disbursement.entity.Disbursement;

public interface DisbursementRepository extends JpaRepository<Disbursement, Long> {

    Optional<Disbursement> findByTravelRequestId(Long travelRequestId);

    boolean existsByTravelRequestId(Long travelRequestId);
}
