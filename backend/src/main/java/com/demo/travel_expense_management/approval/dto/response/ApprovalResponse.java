package com.demo.travel_expense_management.approval.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.demo.travel_expense_management.approval.entity.ApprovalDecision;

public record ApprovalResponse(
    Long id,
    Long travelRequestId,
    Long approverId,
    String approverName,
    ApprovalDecision decision,
    Integer approvedAmount,
    String comments,
    LocalDate decisionDate,
    LocalDateTime createdAt
) {}
