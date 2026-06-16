package com.demo.travel_expense_management.disbursement.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.disbursement.dto.request.RegisterDisbursementRequest;
import com.demo.travel_expense_management.disbursement.dto.response.DisbursementResponse;
import com.demo.travel_expense_management.disbursement.dto.response.DisbursementSummaryResponse;
import com.demo.travel_expense_management.disbursement.entity.Disbursement;
import com.demo.travel_expense_management.disbursement.mapper.DisbursementMapper;
import com.demo.travel_expense_management.disbursement.repository.DisbursementRepository;
import com.demo.travel_expense_management.security.userdetails.CustomUserDetails;
import com.demo.travel_expense_management.travelrequest.entity.RequestStatus;
import com.demo.travel_expense_management.travelrequest.entity.TravelRequest;
import com.demo.travel_expense_management.travelrequest.repository.TravelRequestRepository;
import com.demo.travel_expense_management.user.entity.Role;
import com.demo.travel_expense_management.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisbursementServiceImpl implements DisbursementService {

    private final DisbursementRepository disbursementRepository;
    private final TravelRequestRepository travelRequestRepository;
    private final DisbursementMapper disbursementMapper;

    @Override
    @Transactional
    public DisbursementResponse registerDisbursement(Long travelRequestId, RegisterDisbursementRequest request) {
        TravelRequest travelRequest = travelRequestRepository.findById(travelRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Travel request not found with id: " + travelRequestId));

        if (travelRequest.getStatus() != RequestStatus.APROBADA) {
            throw new BusinessException("Travel request must be approved before disbursement");
        }

        if (disbursementRepository.existsByTravelRequestId(travelRequestId)) {
            throw new BusinessException("Travel request already has a disbursement");
        }

        if (travelRequest.getApprovedAmount() == null) {
            throw new BusinessException("Travel request has no approved amount");
        }

        User currentUser = getCurrentUser();
        if (!currentUser.isActive()) {
            throw new BusinessException("User is not active");
        }
        if (currentUser.getRole() != Role.FINANCIERA && currentUser.getRole() != Role.ADMINISTRADOR) {
            throw new BusinessException("Only FINANCIERA or ADMINISTRADOR can register disbursements");
        }

        if (!request.disbursedAmount().equals(travelRequest.getApprovedAmount())
                && (request.adjustmentJustification() == null || request.adjustmentJustification().isBlank())) {
            throw new BusinessException("Adjustment justification is required when disbursed amount differs from approved amount");
        }

        Disbursement disbursement = new Disbursement();
        disbursement.setTravelRequest(travelRequest);
        disbursement.setRegisteredBy(currentUser);
        disbursement.setRequestedAmount(travelRequest.getRequestedAmount());
        disbursement.setDisbursedAmount(request.disbursedAmount());
        disbursement.setAdjustmentJustification(request.adjustmentJustification());
        disbursement.setPaymentDate(LocalDate.now());

        travelRequest.setStatus(RequestStatus.DESEMBOLSADA);

        disbursementRepository.save(disbursement);

        return disbursementMapper.toResponse(disbursement);
    }

    @Override
    public DisbursementResponse findByTravelRequest(Long travelRequestId) {
        if (!travelRequestRepository.existsById(travelRequestId)) {
            throw new ResourceNotFoundException("Travel request not found with id: " + travelRequestId);
        }
        return disbursementRepository.findByTravelRequestId(travelRequestId)
            .map(disbursementMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Disbursement not found for travel request: " + travelRequestId));
    }

    @Override
    public Page<DisbursementSummaryResponse> findAll(Pageable pageable) {
        return disbursementRepository.findAll(pageable)
            .map(disbursementMapper::toSummaryResponse);
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
