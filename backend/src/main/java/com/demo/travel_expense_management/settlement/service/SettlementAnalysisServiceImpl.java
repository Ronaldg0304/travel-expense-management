package com.demo.travel_expense_management.settlement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.disbursement.repository.DisbursementRepository;
import com.demo.travel_expense_management.legalization.entity.Legalization;
import com.demo.travel_expense_management.legalization.repository.LegalizationRepository;
import com.demo.travel_expense_management.settlement.dto.response.SettlementAnalysisResponse;
import com.demo.travel_expense_management.settlement.type.SettlementType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementAnalysisServiceImpl implements SettlementAnalysisService {

    private final LegalizationRepository legalizationRepository;
    private final DisbursementRepository disbursementRepository;

    @Override
    public SettlementAnalysisResponse analyze(Long legalizationId) {
        Legalization legalization = legalizationRepository.findById(legalizationId)
            .orElseThrow(() -> new ResourceNotFoundException("Legalization not found with id: " + legalizationId));

        var travelRequest = legalization.getTravelRequest();
        if (travelRequest == null) {
            throw new BusinessException("Legalization has no associated travel request");
        }

        var disbursement = disbursementRepository.findByTravelRequestId(travelRequest.getId())
            .orElseThrow(() -> new BusinessException("No disbursement found for the associated travel request"));

        Integer disbursedAmount = disbursement.getDisbursedAmount();
        Integer totalExpenses = legalization.getExpenses() == null
            ? 0
            : legalization.getExpenses().stream().mapToInt(e -> e.getAmount()).sum();

        if (disbursedAmount < 0) {
            throw new BusinessException("Disbursed amount cannot be negative");
        }
        if (totalExpenses < 0) {
            throw new BusinessException("Total expenses cannot be negative");
        }

        int difference = disbursedAmount - totalExpenses;

        SettlementType settlementType;
        if (difference > 0) {
            settlementType = SettlementType.REFUND;
        } else if (difference < 0) {
            settlementType = SettlementType.REIMBURSEMENT;
        } else {
            settlementType = SettlementType.BALANCED;
        }

        return new SettlementAnalysisResponse(
            legalizationId,
            travelRequest.getRequestNumber(),
            disbursedAmount,
            totalExpenses,
            difference,
            settlementType
        );
    }
}
