package com.demo.travel_expense_management.legalization.dto.response;

import java.time.LocalDateTime;

public record SupportFileResponse(
    Long id,
    Long legalizationId,
    String originalFileName,
    String storedFileName,
    String mimeType,
    Long fileSize,
    Integer version,
    boolean active,
    LocalDateTime uploadedAt
) {}
