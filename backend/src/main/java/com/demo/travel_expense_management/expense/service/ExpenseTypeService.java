package com.demo.travel_expense_management.expense.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.travel_expense_management.expense.dto.request.CreateExpenseTypeRequest;
import com.demo.travel_expense_management.expense.dto.request.UpdateExpenseTypeRequest;
import com.demo.travel_expense_management.expense.dto.response.ExpenseTypeResponse;
import com.demo.travel_expense_management.expense.dto.response.ExpenseTypeSummaryResponse;

public interface ExpenseTypeService {

    ExpenseTypeResponse create(CreateExpenseTypeRequest request);

    ExpenseTypeResponse update(Long id, UpdateExpenseTypeRequest request);

    ExpenseTypeResponse findById(Long id);

    Page<ExpenseTypeSummaryResponse> findAll(Pageable pageable);

    void activate(Long id);

    void deactivate(Long id);
}
