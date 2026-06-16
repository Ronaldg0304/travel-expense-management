package com.demo.travel_expense_management.reimbursement.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.account.entity.Account;
import com.demo.travel_expense_management.account.repository.AccountRepository;
import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.legalization.entity.Legalization;
import com.demo.travel_expense_management.legalization.repository.LegalizationRepository;
import com.demo.travel_expense_management.reimbursement.dto.request.RegisterReimbursementRequest;
import com.demo.travel_expense_management.reimbursement.dto.response.ReimbursementResponse;
import com.demo.travel_expense_management.reimbursement.entity.Reimbursement;
import com.demo.travel_expense_management.reimbursement.mapper.ReimbursementMapper;
import com.demo.travel_expense_management.reimbursement.repository.ReimbursementRepository;
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
public class ReimbursementServiceImpl implements ReimbursementService {

    private final LegalizationRepository legalizationRepository;
    private final ReimbursementRepository reimbursementRepository;
    private final AccountRepository accountRepository;
    private final SettlementAnalysisService settlementAnalysisService;
    private final ReimbursementMapper reimbursementMapper;

    @Override
    @Transactional
    public ReimbursementResponse registerReimbursement(Long legalizationId, RegisterReimbursementRequest request) {
        Legalization legalization = legalizationRepository.findById(legalizationId)
            .orElseThrow(() -> new ResourceNotFoundException("Legalization not found with id: " + legalizationId));

        var analysis = settlementAnalysisService.analyze(legalizationId);
        if (analysis.settlementType() != SettlementType.REIMBURSEMENT) {
            throw new BusinessException("Settlement analysis indicates " + analysis.settlementType() + ", expected REIMBURSEMENT");
        }

        if (reimbursementRepository.existsByLegalizationId(legalizationId)) {
            throw new BusinessException("Reimbursement already exists for this legalization");
        }

        Account account = accountRepository.findById(request.accountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + request.accountId()));

        if (!account.isActive()) {
            throw new BusinessException("Account is not active");
        }

        User applicant = legalization.getTravelRequest().getApplicant();
        if (!account.getUser().getId().equals(applicant.getId())) {
            throw new BusinessException("Account does not belong to the request applicant");
        }

        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.FINANCIERA && currentUser.getRole() != Role.ADMINISTRADOR) {
            throw new BusinessException("Only FINANCIERA or ADMINISTRADOR can register reimbursements");
        }

        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setLegalization(legalization);
        reimbursement.setEmployeeAccount(account);
        reimbursement.setAmount(Math.abs(analysis.difference()));
        reimbursement.setPaymentDate(request.paymentDate());
        reimbursement.setReferenceNumber(request.paymentReference());
        reimbursement.setRegisteredBy(currentUser);
        reimbursement.setObservations(request.comments());

        legalization.getTravelRequest().setStatus(RequestStatus.CERRADA);
        reimbursementRepository.save(reimbursement);

        return reimbursementMapper.toResponse(reimbursement);
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
