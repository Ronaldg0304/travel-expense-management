package com.demo.travel_expense_management.approval.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectTravelRequestRequest(
    @NotBlank @Size(max = 1000) String comments
) {}
