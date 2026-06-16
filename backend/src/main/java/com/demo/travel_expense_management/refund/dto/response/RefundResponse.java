package com.demo.travel_expense_management.refund.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RefundResponse(
    Long id,
    Long legalizationId,
    String requestNumber,
    Integer refundAmount,
    String companyAccount,
    String refundReference,
    LocalDate refundDate,
    String comments,
    Long registeredById,
    String registeredByName,
    LocalDateTime createdAt
) {}
