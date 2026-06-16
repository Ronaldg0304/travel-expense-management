package com.demo.travel_expense_management.legalization.controller;

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
import com.demo.travel_expense_management.legalization.dto.request.CreateLegalizationRequest;
import com.demo.travel_expense_management.legalization.dto.response.LegalizationResponse;
import com.demo.travel_expense_management.legalization.dto.response.LegalizationSummaryResponse;
import com.demo.travel_expense_management.legalization.service.LegalizationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/legalizations")
@RequiredArgsConstructor
@Tag(name = "Legalizations", description = "Legalization management API")
public class LegalizationController {

    private final LegalizationService legalizationService;

    @PostMapping
    @Operation(summary = "Submit a legalization for a travel request")
    public ResponseEntity<ApiResponse<LegalizationResponse>> create(@Valid @RequestBody CreateLegalizationRequest request) {
        LegalizationResponse response = legalizationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get legalization by ID")
    public ResponseEntity<ApiResponse<LegalizationResponse>> findById(@PathVariable Long id) {
        LegalizationResponse response = legalizationService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/travel-request/{travelRequestId}")
    @Operation(summary = "Get legalization by travel request ID")
    public ResponseEntity<ApiResponse<LegalizationResponse>> findByTravelRequest(@PathVariable Long travelRequestId) {
        LegalizationResponse response = legalizationService.findByTravelRequest(travelRequestId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of legalizations")
    public ResponseEntity<ApiResponse<Page<LegalizationSummaryResponse>>> findAll(Pageable pageable) {
        Page<LegalizationSummaryResponse> response = legalizationService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
