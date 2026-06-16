package com.demo.travel_expense_management.costcenter.controller;

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
import com.demo.travel_expense_management.costcenter.dto.request.CreateCostCenterRequest;
import com.demo.travel_expense_management.costcenter.dto.request.UpdateCostCenterRequest;
import com.demo.travel_expense_management.costcenter.dto.response.CostCenterResponse;
import com.demo.travel_expense_management.costcenter.dto.response.CostCenterSummaryResponse;
import com.demo.travel_expense_management.costcenter.service.CostCenterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cost-centers")
@RequiredArgsConstructor
@Tag(name = "Cost Centers", description = "Cost center management API")
public class CostCenterController {

    private final CostCenterService costCenterService;

    @PostMapping
    @Operation(summary = "Create a new cost center")
    public ResponseEntity<ApiResponse<CostCenterResponse>> create(@Valid @RequestBody CreateCostCenterRequest request) {
        CostCenterResponse response = costCenterService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing cost center")
    public ResponseEntity<ApiResponse<CostCenterResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCostCenterRequest request) {
        CostCenterResponse response = costCenterService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cost center by ID")
    public ResponseEntity<ApiResponse<CostCenterResponse>> findById(@PathVariable Long id) {
        CostCenterResponse response = costCenterService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of cost centers")
    public ResponseEntity<ApiResponse<Page<CostCenterSummaryResponse>>> findAll(Pageable pageable) {
        Page<CostCenterSummaryResponse> response = costCenterService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a cost center")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Long id) {
        costCenterService.activate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Cost center activated", null));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a cost center")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        costCenterService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Cost center deactivated", null));
    }
}
