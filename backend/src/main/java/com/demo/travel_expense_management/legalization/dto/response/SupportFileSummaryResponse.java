package com.demo.travel_expense_management.legalization.dto.response;

import java.time.LocalDateTime;

public record SupportFileSummaryResponse(
    Long id,
    String originalFileName,
    Integer version,
    LocalDateTime uploadedAt
) {}
