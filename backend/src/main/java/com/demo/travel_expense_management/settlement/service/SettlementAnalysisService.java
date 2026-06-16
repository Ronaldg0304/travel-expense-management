package com.demo.travel_expense_management.settlement.service;

import com.demo.travel_expense_management.settlement.dto.response.SettlementAnalysisResponse;

public interface SettlementAnalysisService {

    SettlementAnalysisResponse analyze(Long legalizationId);
}
