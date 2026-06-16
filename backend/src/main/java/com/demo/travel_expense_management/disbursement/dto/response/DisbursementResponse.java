package com.demo.travel_expense_management.disbursement.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DisbursementResponse(
    Long id,
    Long travelRequestId,
    String requestNumber,
    String applicantName,
    Integer approvedAmount,
    Integer disbursedAmount,
    String adjustmentJustification,
    Long registeredById,
    String registeredByName,
    LocalDate disbursementDate,
    LocalDateTime createdAt
) {}
