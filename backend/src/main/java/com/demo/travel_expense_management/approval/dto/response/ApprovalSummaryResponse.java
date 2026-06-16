package com.demo.travel_expense_management.approval.dto.response;

import java.time.LocalDate;

import com.demo.travel_expense_management.approval.entity.ApprovalDecision;

public record ApprovalSummaryResponse(
    Long id,
    String approverName,
    ApprovalDecision decision,
    LocalDate decisionDate
) {}
