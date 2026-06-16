package com.demo.travel_expense_management.disbursement.dto.response;

import java.time.LocalDate;

public record DisbursementSummaryResponse(
    Long id,
    String requestNumber,
    String applicantName,
    Integer disbursedAmount,
    LocalDate disbursementDate
) {}
