package com.demo.travel_expense_management.travelrequest.service;

import java.time.Year;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.department.repository.DepartmentApproverRepository;
import com.demo.travel_expense_management.department.repository.DepartmentRepository;
import com.demo.travel_expense_management.security.userdetails.CustomUserDetails;
import com.demo.travel_expense_management.travelrequest.dto.request.CreateTravelRequestRequest;
import com.demo.travel_expense_management.travelrequest.dto.request.UpdateTravelRequestRequest;
import com.demo.travel_expense_management.travelrequest.dto.response.TravelRequestResponse;
import com.demo.travel_expense_management.travelrequest.dto.response.TravelRequestSummaryResponse;
import com.demo.travel_expense_management.travelrequest.entity.RequestStatus;
import com.demo.travel_expense_management.travelrequest.entity.TravelRequest;
import com.demo.travel_expense_management.travelrequest.mapper.TravelRequestMapper;
import com.demo.travel_expense_management.travelrequest.repository.TravelRequestRepository;
import com.demo.travel_expense_management.user.entity.User;
import com.demo.travel_expense_management.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelRequestServiceImpl implements TravelRequestService {

    private static final String REQUEST_NUMBER_PREFIX = "TRV-";
    private static final long REQUEST_NUMBER_PAD = 6;

    private final TravelRequestRepository travelRequestRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentApproverRepository departmentApproverRepository;
    private final TravelRequestMapper travelRequestMapper;

    @Override
    @Transactional
    public TravelRequestResponse createDraft(CreateTravelRequestRequest request) {
        User currentUser = getCurrentUser();

        TravelRequest travelRequest = travelRequestMapper.toEntity(request);
        travelRequest.setApplicant(currentUser);
        travelRequest.setStatus(RequestStatus.BORRADOR);
        travelRequest.setRequestNumber(generateRequestNumber());

        return travelRequestMapper.toResponse(travelRequestRepository.save(travelRequest));
    }

    @Override
    @Transactional
    public TravelRequestResponse updateDraft(Long id, UpdateTravelRequestRequest request) {
        TravelRequest travelRequest = travelRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Travel request not found with id: " + id));
        User currentUser = getCurrentUser();

        if (travelRequest.getStatus() != RequestStatus.BORRADOR) {
            throw new BusinessException("Only draft requests can be updated");
        }
        if (!travelRequest.getApplicant().getId().equals(currentUser.getId())) {
            throw new BusinessException("Only the owner can update this request");
        }

        travelRequestMapper.updateEntity(request, travelRequest);

        return travelRequestMapper.toResponse(travelRequestRepository.save(travelRequest));
    }

    @Override
    public TravelRequestResponse findById(Long id) {
        return travelRequestRepository.findById(id)
            .map(travelRequestMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Travel request not found with id: " + id));
    }

    @Override
    public Page<TravelRequestSummaryResponse> findAll(Pageable pageable) {
        return travelRequestRepository.findAll(pageable)
            .map(travelRequestMapper::toSummaryResponse);
    }

    @Override
    public Page<TravelRequestSummaryResponse> findMyRequests(Pageable pageable) {
        User currentUser = getCurrentUser();
        return travelRequestRepository.findByApplicantId(currentUser.getId(), pageable)
            .map(travelRequestMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    public TravelRequestResponse submitForApproval(Long id) {
        TravelRequest travelRequest = travelRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Travel request not found with id: " + id));
        User currentUser = getCurrentUser();

        if (travelRequest.getStatus() != RequestStatus.BORRADOR) {
            throw new BusinessException("Only draft requests can be submitted");
        }
        if (!travelRequest.getApplicant().getId().equals(currentUser.getId())) {
            throw new BusinessException("Only the owner can submit this request");
        }

        departmentRepository.findById(travelRequest.getApplicant().getDepartment().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Applicant department not found"));

        List<com.demo.travel_expense_management.department.entity.DepartmentApprover> approvers =
            departmentApproverRepository.findByDepartmentId(travelRequest.getApplicant().getDepartment().getId());
        boolean hasActiveApprover = approvers.stream().anyMatch(
            com.demo.travel_expense_management.department.entity.DepartmentApprover::isActive);
        if (!hasActiveApprover) {
            throw new BusinessException("Department must have at least one active approver");
        }

        travelRequest.setStatus(RequestStatus.ENVIADA);

        return travelRequestMapper.toResponse(travelRequestRepository.save(travelRequest));
    }

    @Override
    @Transactional
    public TravelRequestResponse cancelDraft(Long id) {
        TravelRequest travelRequest = travelRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Travel request not found with id: " + id));
        User currentUser = getCurrentUser();

        if (travelRequest.getStatus() != RequestStatus.BORRADOR) {
            throw new BusinessException("Only draft requests can be cancelled");
        }
        if (!travelRequest.getApplicant().getId().equals(currentUser.getId())) {
            throw new BusinessException("Only the owner can cancel this request");
        }

        travelRequest.setStatus(RequestStatus.RECHAZADA);

        return travelRequestMapper.toResponse(travelRequestRepository.save(travelRequest));
    }

    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User must be logged in");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.user();
    }

    private String generateRequestNumber() {
        String year = String.valueOf(Year.now().getValue());
        long nextNumber = travelRequestRepository.count() + 1;
        return REQUEST_NUMBER_PREFIX + year + "-" + String.format("%0" + REQUEST_NUMBER_PAD + "d", nextNumber);
    }
}
