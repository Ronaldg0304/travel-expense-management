package com.demo.travel_expense_management.expense.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.travel_expense_management.common.response.ApiResponse;
import com.demo.travel_expense_management.expense.dto.request.CreateExpenseTypeRequest;
import com.demo.travel_expense_management.expense.dto.request.UpdateExpenseTypeRequest;
import com.demo.travel_expense_management.expense.dto.response.ExpenseTypeResponse;
import com.demo.travel_expense_management.expense.dto.response.ExpenseTypeSummaryResponse;
import com.demo.travel_expense_management.expense.service.ExpenseTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/expense-types")
@RequiredArgsConstructor
@Tag(name = "Expense Types", description = "Expense type catalog API")
public class ExpenseTypeController {

    private final ExpenseTypeService expenseTypeService;

    @PostMapping
    @Operation(summary = "Create a new expense type")
    public ResponseEntity<ApiResponse<ExpenseTypeResponse>> create(@Valid @RequestBody CreateExpenseTypeRequest request) {
        ExpenseTypeResponse response = expenseTypeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing expense type")
    public ResponseEntity<ApiResponse<ExpenseTypeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpenseTypeRequest request) {
        ExpenseTypeResponse response = expenseTypeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense type by ID")
    public ResponseEntity<ApiResponse<ExpenseTypeResponse>> findById(@PathVariable Long id) {
        ExpenseTypeResponse response = expenseTypeService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of expense types")
    public ResponseEntity<ApiResponse<Page<ExpenseTypeSummaryResponse>>> findAll(Pageable pageable) {
        Page<ExpenseTypeSummaryResponse> response = expenseTypeService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate an expense type")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Long id) {
        expenseTypeService.activate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Expense type activated", null));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate an expense type")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        expenseTypeService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Expense type deactivated", null));
    }
}
