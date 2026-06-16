package com.demo.travel_expense_management.legalization.dto.response;

import com.demo.travel_expense_management.travelrequest.entity.RequestStatus;

public record LegalizationSummaryResponse(
    Long id,
    String requestNumber,
    String applicantName,
    Integer totalExpenses,
    RequestStatus status
) {}
