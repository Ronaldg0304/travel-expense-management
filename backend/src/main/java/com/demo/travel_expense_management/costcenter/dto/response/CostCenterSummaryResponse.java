package com.demo.travel_expense_management.costcenter.dto.response;

import com.demo.travel_expense_management.costcenter.entity.CostCenterType;

public record CostCenterSummaryResponse(
    Long id,
    String code,
    String name,
    CostCenterType type,
    boolean active
) {}
