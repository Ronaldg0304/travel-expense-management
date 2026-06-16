package com.demo.travel_expense_management.department.controller;

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
import com.demo.travel_expense_management.department.dto.request.CreateDepartmentRequest;
import com.demo.travel_expense_management.department.dto.request.UpdateDepartmentRequest;
import com.demo.travel_expense_management.department.dto.response.DepartmentResponse;
import com.demo.travel_expense_management.department.dto.response.DepartmentSummaryResponse;
import com.demo.travel_expense_management.department.service.DepartmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Department management API")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "Create a new department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(@Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentResponse response = departmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        DepartmentResponse response = departmentService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<ApiResponse<DepartmentResponse>> findById(@PathVariable Long id) {
        DepartmentResponse response = departmentService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of departments")
    public ResponseEntity<ApiResponse<Page<DepartmentSummaryResponse>>> findAll(Pageable pageable) {
        Page<DepartmentSummaryResponse> response = departmentService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a department")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Long id) {
        departmentService.activate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Department activated", null));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a department")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        departmentService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Department deactivated", null));
    }
}
