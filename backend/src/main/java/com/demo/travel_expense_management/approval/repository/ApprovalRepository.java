package com.demo.travel_expense_management.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.travel_expense_management.approval.entity.Approval;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    List<Approval> findByTravelRequestIdOrderByCreatedAtAsc(Long travelRequestId);

    boolean existsByTravelRequestIdAndApproverId(Long travelRequestId, Long approverId);
}
