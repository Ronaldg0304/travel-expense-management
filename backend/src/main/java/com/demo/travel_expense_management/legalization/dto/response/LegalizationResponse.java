package com.demo.travel_expense_management.legalization.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.demo.travel_expense_management.expense.dto.response.ExpenseResponse;
import com.demo.travel_expense_management.travelrequest.entity.RequestStatus;

public record LegalizationResponse(
    Long id,
    Long travelRequestId,
    String requestNumber,
    String applicantName,
    Long costCenterId,
    String costCenterName,
    Integer totalExpenses,
    RequestStatus status,
    LocalDateTime submittedAt,
    List<ExpenseResponse> expenses
) {}
