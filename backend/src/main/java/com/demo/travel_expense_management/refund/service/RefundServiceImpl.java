package com.demo.travel_expense_management.refund.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.legalization.entity.Legalization;
import com.demo.travel_expense_management.legalization.repository.LegalizationRepository;
import com.demo.travel_expense_management.refund.dto.request.RegisterRefundRequest;
import com.demo.travel_expense_management.refund.dto.response.RefundResponse;
import com.demo.travel_expense_management.refund.entity.Refund;
import com.demo.travel_expense_management.refund.mapper.RefundMapper;
import com.demo.travel_expense_management.refund.repository.RefundRepository;
import com.demo.travel_expense_management.security.userdetails.CustomUserDetails;
import com.demo.travel_expense_management.settlement.service.SettlementAnalysisService;
import com.demo.travel_expense_management.settlement.type.SettlementType;
import com.demo.travel_expense_management.travelrequest.entity.RequestStatus;
import com.demo.travel_expense_management.user.entity.Role;
import com.demo.travel_expense_management.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundServiceImpl implements RefundService {

    private final LegalizationRepository legalizationRepository;
    private final RefundRepository refundRepository;
    private final SettlementAnalysisService settlementAnalysisService;
    private final RefundMapper refundMapper;

    @Override
    @Transactional
    public RefundResponse registerRefund(Long legalizationId, RegisterRefundRequest request) {
        Legalization legalization = legalizationRepository.findById(legalizationId)
            .orElseThrow(() -> new ResourceNotFoundException("Legalization not found with id: " + legalizationId));

        var analysis = settlementAnalysisService.analyze(legalizationId);
        if (analysis.settlementType() != SettlementType.REFUND) {
            throw new BusinessException("Settlement analysis indicates " + analysis.settlementType() + ", expected REFUND");
        }

        if (refundRepository.existsByLegalizationId(legalizationId)) {
            throw new BusinessException("Refund already exists for this legalization");
        }

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.FINANCIERA && currentUser.getRole() != Role.ADMINISTRADOR) {
            throw new BusinessException("Only FINANCIERA or ADMINISTRADOR can register refunds");
        }

        Refund refund = new Refund();
        refund.setLegalization(legalization);
        refund.setCompanyAccount(request.companyAccount());
        refund.setAmount(analysis.difference());
        refund.setPaymentDate(request.refundDate());
        refund.setReferenceNumber(request.refundReference());
        refund.setRegisteredBy(currentUser);
        refund.setObservations(request.comments());

        legalization.getTravelRequest().setStatus(RequestStatus.CERRADA);
        refundRepository.save(refund);

        return refundMapper.toResponse(refund);
    }

    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User must be logged in");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.user();
    }
}
