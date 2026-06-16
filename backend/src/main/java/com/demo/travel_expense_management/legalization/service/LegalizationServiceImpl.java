package com.demo.travel_expense_management.legalization.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.costcenter.repository.CostCenterRepository;
import com.demo.travel_expense_management.expense.entity.Expense;
import com.demo.travel_expense_management.expense.repository.ExpenseRepository;
import com.demo.travel_expense_management.expense.repository.ExpenseTypeRepository;
import com.demo.travel_expense_management.legalization.dto.request.CreateExpenseRequest;
import com.demo.travel_expense_management.legalization.dto.request.CreateLegalizationRequest;
import com.demo.travel_expense_management.legalization.dto.response.LegalizationResponse;
import com.demo.travel_expense_management.legalization.dto.response.LegalizationSummaryResponse;
import com.demo.travel_expense_management.legalization.entity.Legalization;
import com.demo.travel_expense_management.legalization.mapper.LegalizationMapper;
import com.demo.travel_expense_management.legalization.repository.LegalizationRepository;
import com.demo.travel_expense_management.legalization.repository.SupportFileRepository;
import com.demo.travel_expense_management.security.userdetails.CustomUserDetails;
import com.demo.travel_expense_management.storage.service.FileStorageService;
import com.demo.travel_expense_management.travelrequest.entity.RequestStatus;
import com.demo.travel_expense_management.travelrequest.entity.TravelRequest;
import com.demo.travel_expense_management.travelrequest.repository.TravelRequestRepository;
import com.demo.travel_expense_management.user.entity.User;
import com.demo.travel_expense_management.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LegalizationServiceImpl implements LegalizationService {

    private final LegalizationRepository legalizationRepository;
    private final ExpenseRepository expenseRepository;
    private final SupportFileRepository supportFileRepository;
    private final TravelRequestRepository travelRequestRepository;
    private final CostCenterRepository costCenterRepository;
    private final ExpenseTypeRepository expenseTypeRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final LegalizationMapper legalizationMapper;
    private final com.demo.travel_expense_management.expense.mapper.ExpenseMapper expenseMapper;

    @Override
    @Transactional
    public LegalizationResponse create(CreateLegalizationRequest request) {
        TravelRequest travelRequest = travelRequestRepository.findById(request.travelRequestId())
            .orElseThrow(() -> new ResourceNotFoundException("Travel request not found with id: " + request.travelRequestId()));

        if (travelRequest.getStatus() != RequestStatus.DESEMBOLSADA) {
            throw new BusinessException("Travel request must be disbursed before legalization");
        }

        if (legalizationRepository.existsByTravelRequestId(request.travelRequestId())) {
            throw new BusinessException("Legalization already exists for this travel request");
        }

        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(travelRequest.getApplicant().getId())) {
            throw new BusinessException("Only the request owner can submit a legalization");
        }

        var costCenter = costCenterRepository.findById(request.costCenterId())
            .orElseThrow(() -> new ResourceNotFoundException("Cost center not found with id: " + request.costCenterId()));

        if (!costCenter.isActive()) {
            throw new BusinessException("Cost center is not active");
        }

        if (request.expenses() == null || request.expenses().isEmpty()) {
            throw new BusinessException("At least one expense is required");
        }

        List<Expense> expenses = new ArrayList<>();
        for (CreateExpenseRequest expenseRequest : request.expenses()) {
            var expenseType = expenseTypeRepository.findById(expenseRequest.expenseTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense type not found with id: " + expenseRequest.expenseTypeId()));

            if (!expenseType.isActive()) {
                throw new BusinessException("Expense type is not active: " + expenseType.getName());
            }

            if (expenseRequest.expenseDate().isAfter(LocalDate.now())) {
                throw new BusinessException("Expense date cannot be in the future");
            }

            Expense expense = new Expense();
            expense.setExpenseType(expenseType);
            expense.setExpenseDate(expenseRequest.expenseDate());
            expense.setDescription(expenseRequest.description());
            expense.setAmount(expenseRequest.amount());
            expenses.add(expense);
        }

        Legalization legalization = new Legalization();
        legalization.setTravelRequest(travelRequest);
        legalization.setCostCenter(costCenter);
        legalization.setSubmittedBy(currentUser);
        legalization.setSubmittedAt(LocalDateTime.now());
        legalization.setExpenses(expenses);
        expenses.forEach(e -> e.setLegalization(legalization));

        travelRequest.setStatus(RequestStatus.LEGALIZADA);

        legalizationRepository.save(legalization);

        return legalizationMapper.toResponse(legalization);
    }

    @Override
    public LegalizationResponse findById(Long id) {
        return legalizationRepository.findById(id)
            .map(legalizationMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Legalization not found with id: " + id));
    }

    @Override
    public LegalizationResponse findByTravelRequest(Long travelRequestId) {
        if (!travelRequestRepository.existsById(travelRequestId)) {
            throw new ResourceNotFoundException("Travel request not found with id: " + travelRequestId);
        }
        return legalizationRepository.findByTravelRequestId(travelRequestId)
            .map(legalizationMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Legalization not found for travel request: " + travelRequestId));
    }

    @Override
    public Page<LegalizationSummaryResponse> findAll(Pageable pageable) {
        return legalizationRepository.findAll(pageable)
            .map(legalizationMapper::toSummaryResponse);
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
