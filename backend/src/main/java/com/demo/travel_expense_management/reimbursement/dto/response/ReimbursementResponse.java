package com.demo.travel_expense_management.reimbursement.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReimbursementResponse(
    Long id,
    Long legalizationId,
    String requestNumber,
    Integer reimbursementAmount,
    Long accountId,
    String accountNumber,
    String paymentReference,
    LocalDate paymentDate,
    String comments,
    Long registeredById,
    String registeredByName,
    LocalDateTime createdAt
) {}
