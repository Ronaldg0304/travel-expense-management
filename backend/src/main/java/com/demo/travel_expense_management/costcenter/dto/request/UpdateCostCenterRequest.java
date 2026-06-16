package com.demo.travel_expense_management.costcenter.dto.request;

import com.demo.travel_expense_management.costcenter.entity.CostCenterType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCostCenterRequest(
    @NotBlank @Size(max = 100) String name,
    @NotNull CostCenterType type,
    boolean active
) {}
