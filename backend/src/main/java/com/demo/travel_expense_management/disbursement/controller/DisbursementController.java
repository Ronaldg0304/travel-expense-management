package com.demo.travel_expense_management.disbursement.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.travel_expense_management.common.response.ApiResponse;
import com.demo.travel_expense_management.disbursement.dto.request.RegisterDisbursementRequest;
import com.demo.travel_expense_management.disbursement.dto.response.DisbursementResponse;
import com.demo.travel_expense_management.disbursement.dto.response.DisbursementSummaryResponse;
import com.demo.travel_expense_management.disbursement.service.DisbursementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/disbursements")
@RequiredArgsConstructor
@Tag(name = "Disbursements", description = "Disbursement management API")
public class DisbursementController {

    private final DisbursementService disbursementService;

    @PostMapping("/travel-request/{id}")
    @Operation(summary = "Register a disbursement for a travel request")
    public ResponseEntity<ApiResponse<DisbursementResponse>> registerDisbursement(
            @PathVariable Long id,
            @Valid @RequestBody RegisterDisbursementRequest request) {
        DisbursementResponse response = disbursementService.registerDisbursement(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/travel-request/{id}")
    @Operation(summary = "Get disbursement by travel request ID")
    public ResponseEntity<ApiResponse<DisbursementResponse>> findByTravelRequest(@PathVariable Long id) {
        DisbursementResponse response = disbursementService.findByTravelRequest(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of disbursements")
    public ResponseEntity<ApiResponse<Page<DisbursementSummaryResponse>>> findAll(Pageable pageable) {
        Page<DisbursementSummaryResponse> response = disbursementService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
