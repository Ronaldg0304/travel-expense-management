package com.demo.travel_expense_management.travelrequest.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.travel_expense_management.common.response.ApiResponse;
import com.demo.travel_expense_management.travelrequest.dto.request.CreateTravelRequestRequest;
import com.demo.travel_expense_management.travelrequest.dto.request.UpdateTravelRequestRequest;
import com.demo.travel_expense_management.travelrequest.dto.response.TravelRequestResponse;
import com.demo.travel_expense_management.travelrequest.dto.response.TravelRequestSummaryResponse;
import com.demo.travel_expense_management.travelrequest.service.TravelRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/travel-requests")
@RequiredArgsConstructor
@Tag(name = "Travel Requests", description = "Travel request management API")
public class TravelRequestController {

    private final TravelRequestService travelRequestService;

    @PostMapping("/draft")
    @Operation(summary = "Create a new travel request draft")
    public ResponseEntity<ApiResponse<TravelRequestResponse>> createDraft(
            @Valid @RequestBody CreateTravelRequestRequest request) {
        TravelRequestResponse response = travelRequestService.createDraft(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}/draft")
    @Operation(summary = "Update a travel request draft")
    public ResponseEntity<ApiResponse<TravelRequestResponse>> updateDraft(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTravelRequestRequest request) {
        TravelRequestResponse response = travelRequestService.updateDraft(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit a travel request for approval")
    public ResponseEntity<ApiResponse<TravelRequestResponse>> submitForApproval(@PathVariable Long id) {
        TravelRequestResponse response = travelRequestService.submitForApproval(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a travel request draft")
    public ResponseEntity<ApiResponse<TravelRequestResponse>> cancelDraft(@PathVariable Long id) {
        TravelRequestResponse response = travelRequestService.cancelDraft(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get travel request by ID")
    public ResponseEntity<ApiResponse<TravelRequestResponse>> findById(@PathVariable Long id) {
        TravelRequestResponse response = travelRequestService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of travel requests")
    public ResponseEntity<ApiResponse<Page<TravelRequestSummaryResponse>>> findAll(Pageable pageable) {
        Page<TravelRequestSummaryResponse> response = travelRequestService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my")
    @Operation(summary = "Get paginated list of my travel requests")
    public ResponseEntity<ApiResponse<Page<TravelRequestSummaryResponse>>> findMyRequests(Pageable pageable) {
        Page<TravelRequestSummaryResponse> response = travelRequestService.findMyRequests(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
