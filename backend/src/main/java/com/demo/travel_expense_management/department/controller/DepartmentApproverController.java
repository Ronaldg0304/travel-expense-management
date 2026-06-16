package com.demo.travel_expense_management.department.controller;

import java.util.List;

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
import com.demo.travel_expense_management.department.dto.request.CreateDepartmentApproverRequest;
import com.demo.travel_expense_management.department.dto.request.UpdateDepartmentApproverRequest;
import com.demo.travel_expense_management.department.dto.response.DepartmentApproverResponse;
import com.demo.travel_expense_management.department.dto.response.DepartmentApproverSummaryResponse;
import com.demo.travel_expense_management.department.service.DepartmentApproverService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/department-approvers")
@RequiredArgsConstructor
@Tag(name = "Department Approvers", description = "Department approver assignment API")
public class DepartmentApproverController {

    private final DepartmentApproverService departmentApproverService;

    @PostMapping
    @Operation(summary = "Assign an approver to a department")
    public ResponseEntity<ApiResponse<DepartmentApproverResponse>> create(
            @Valid @RequestBody CreateDepartmentApproverRequest request) {
        DepartmentApproverResponse response = departmentApproverService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a department approver assignment")
    public ResponseEntity<ApiResponse<DepartmentApproverResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentApproverRequest request) {
        DepartmentApproverResponse response = departmentApproverService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department approver by ID")
    public ResponseEntity<ApiResponse<DepartmentApproverResponse>> findById(@PathVariable Long id) {
        DepartmentApproverResponse response = departmentApproverService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of department approvers")
    public ResponseEntity<ApiResponse<Page<DepartmentApproverSummaryResponse>>> findAll(Pageable pageable) {
        Page<DepartmentApproverSummaryResponse> response = departmentApproverService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get approvers by department ID")
    public ResponseEntity<ApiResponse<List<DepartmentApproverSummaryResponse>>> findByDepartment(
            @PathVariable Long departmentId) {
        List<DepartmentApproverSummaryResponse> response = departmentApproverService.findByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a department approver")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Long id) {
        departmentApproverService.activate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Department approver activated", null));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a department approver")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        departmentApproverService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Department approver deactivated", null));
    }
}
