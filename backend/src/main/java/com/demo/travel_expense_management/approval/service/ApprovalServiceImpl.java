package com.demo.travel_expense_management.approval.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.approval.dto.request.ApproveTravelRequestRequest;
import com.demo.travel_expense_management.approval.dto.request.RejectTravelRequestRequest;
import com.demo.travel_expense_management.approval.dto.response.ApprovalResponse;
import com.demo.travel_expense_management.approval.dto.response.ApprovalSummaryResponse;
import com.demo.travel_expense_management.approval.entity.Approval;
import com.demo.travel_expense_management.approval.entity.ApprovalDecision;
import com.demo.travel_expense_management.approval.mapper.ApprovalMapper;
import com.demo.travel_expense_management.approval.repository.ApprovalRepository;
import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.department.repository.DepartmentApproverRepository;
import com.demo.travel_expense_management.security.userdetails.CustomUserDetails;
import com.demo.travel_expense_management.travelrequest.entity.RequestStatus;
import com.demo.travel_expense_management.travelrequest.entity.TravelRequest;
import com.demo.travel_expense_management.travelrequest.repository.TravelRequestRepository;
import com.demo.travel_expense_management.user.entity.User;
import com.demo.travel_expense_management.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final TravelRequestRepository travelRequestRepository;
    private final DepartmentApproverRepository departmentApproverRepository;
    private final ApprovalMapper approvalMapper;

    @Override
    @Transactional
    public ApprovalResponse approve(Long travelRequestId, ApproveTravelRequestRequest request) {
        TravelRequest travelRequest = travelRequestRepository.findById(travelRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Travel request not found with id: " + travelRequestId));

        if (travelRequest.getStatus() != RequestStatus.ENVIADA) {
            throw new BusinessException("Travel request is not pending approval");
        }

        User approver = getCurrentUser();

        if (!approver.isActive()) {
            throw new BusinessException("User is not active");
        }

        Long departmentId = travelRequest.getApplicant().getDepartment().getId();
        if (!departmentApproverRepository.existsByDepartmentIdAndApproverId(departmentId, approver.getId())) {
            throw new BusinessException("User is not an approver for the applicant's department");
        }

        if (approvalRepository.existsByTravelRequestIdAndApproverId(travelRequestId, approver.getId())) {
            throw new BusinessException("User has already approved this request");
        }

        Approval approval = new Approval();
        approval.setTravelRequest(travelRequest);
        approval.setApprover(approver);
        approval.setDecision(ApprovalDecision.APROBADA);
        approval.setDecisionDate(LocalDate.now());

        travelRequest.setApprovedAmount(request.approvedAmount());
        travelRequest.setStatus(RequestStatus.APROBADA);

        approvalRepository.save(approval);

        return approvalMapper.toResponse(approval);
    }

    @Override
    @Transactional
    public ApprovalResponse reject(Long travelRequestId, RejectTravelRequestRequest request) {
        TravelRequest travelRequest = travelRequestRepository.findById(travelRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Travel request not found with id: " + travelRequestId));

        if (travelRequest.getStatus() != RequestStatus.ENVIADA) {
            throw new BusinessException("Travel request is not pending approval");
        }

        User approver = getCurrentUser();

        if (!approver.isActive()) {
            throw new BusinessException("User is not active");
        }

        Long departmentId = travelRequest.getApplicant().getDepartment().getId();
        if (!departmentApproverRepository.existsByDepartmentIdAndApproverId(departmentId, approver.getId())) {
            throw new BusinessException("User is not an approver for the applicant's department");
        }

        if (approvalRepository.existsByTravelRequestIdAndApproverId(travelRequestId, approver.getId())) {
            throw new BusinessException("User has already approved this request");
        }

        Approval approval = new Approval();
        approval.setTravelRequest(travelRequest);
        approval.setApprover(approver);
        approval.setDecision(ApprovalDecision.RECHAZADA);
        approval.setComments(request.comments());
        approval.setDecisionDate(LocalDate.now());

        travelRequest.setStatus(RequestStatus.RECHAZADA);

        approvalRepository.save(approval);

        return approvalMapper.toResponse(approval);
    }

    @Override
    public List<ApprovalSummaryResponse> findHistory(Long travelRequestId) {
        return approvalMapper.toSummaryResponseList(
            approvalRepository.findByTravelRequestIdOrderByCreatedAtAsc(travelRequestId));
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
