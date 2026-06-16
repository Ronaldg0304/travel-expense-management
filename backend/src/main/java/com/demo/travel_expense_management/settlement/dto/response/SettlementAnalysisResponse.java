package com.demo.travel_expense_management.settlement.dto.response;

import com.demo.travel_expense_management.settlement.type.SettlementType;

public record SettlementAnalysisResponse(
    Long legalizationId,
    String requestNumber,
    Integer disbursedAmount,
    Integer totalExpenses,
    Integer difference,
    SettlementType settlementType
) {}
