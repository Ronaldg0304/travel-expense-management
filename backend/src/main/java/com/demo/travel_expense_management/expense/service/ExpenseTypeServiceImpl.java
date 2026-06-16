package com.demo.travel_expense_management.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.expense.dto.request.CreateExpenseTypeRequest;
import com.demo.travel_expense_management.expense.dto.request.UpdateExpenseTypeRequest;
import com.demo.travel_expense_management.expense.dto.response.ExpenseTypeResponse;
import com.demo.travel_expense_management.expense.dto.response.ExpenseTypeSummaryResponse;
import com.demo.travel_expense_management.expense.entity.ExpenseType;
import com.demo.travel_expense_management.expense.mapper.ExpenseTypeMapper;
import com.demo.travel_expense_management.expense.repository.ExpenseTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseTypeServiceImpl implements ExpenseTypeService {

    private final ExpenseTypeRepository expenseTypeRepository;
    private final ExpenseTypeMapper expenseTypeMapper;

    @Override
    @Transactional
    public ExpenseTypeResponse create(CreateExpenseTypeRequest request) {
        if (expenseTypeRepository.existsByCode(request.code())) {
            throw new BusinessException("Expense type code already exists: " + request.code());
        }

        ExpenseType expenseType = expenseTypeMapper.toEntity(request);
        expenseType.setActive(request.active());

        return expenseTypeMapper.toResponse(expenseTypeRepository.save(expenseType));
    }

    @Override
    @Transactional
    public ExpenseTypeResponse update(Long id, UpdateExpenseTypeRequest request) {
        ExpenseType expenseType = expenseTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Expense type not found with id: " + id));

        expenseTypeMapper.updateEntity(request, expenseType);

        return expenseTypeMapper.toResponse(expenseTypeRepository.save(expenseType));
    }

    @Override
    public ExpenseTypeResponse findById(Long id) {
        return expenseTypeRepository.findById(id)
            .map(expenseTypeMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Expense type not found with id: " + id));
    }

    @Override
    public Page<ExpenseTypeSummaryResponse> findAll(Pageable pageable) {
        return expenseTypeRepository.findAll(pageable)
            .map(expenseTypeMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        ExpenseType expenseType = expenseTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Expense type not found with id: " + id));
        expenseType.setActive(true);
        expenseTypeRepository.save(expenseType);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        ExpenseType expenseType = expenseTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Expense type not found with id: " + id));
        expenseType.setActive(false);
        expenseTypeRepository.save(expenseType);
    }
}
