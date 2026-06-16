package com.demo.travel_expense_management.costcenter.dto.response;

import java.time.LocalDateTime;

import com.demo.travel_expense_management.costcenter.entity.CostCenterType;

public record CostCenterResponse(
    Long id,
    String code,
    String name,
    CostCenterType type,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
