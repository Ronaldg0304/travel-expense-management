package com.demo.travel_expense_management.approval.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.travel_expense_management.approval.dto.request.ApproveTravelRequestRequest;
import com.demo.travel_expense_management.approval.dto.request.RejectTravelRequestRequest;
import com.demo.travel_expense_management.approval.dto.response.ApprovalResponse;
import com.demo.travel_expense_management.approval.dto.response.ApprovalSummaryResponse;
import com.demo.travel_expense_management.approval.service.ApprovalService;
import com.demo.travel_expense_management.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/approvals")
@RequiredArgsConstructor
@Tag(name = "Approvals", description = "Approval management API")
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/travel-request/{id}/approve")
    @Operation(summary = "Approve a travel request")
    public ResponseEntity<ApiResponse<ApprovalResponse>> approve(
            @PathVariable Long id,
            @Valid @RequestBody ApproveTravelRequestRequest request) {
        ApprovalResponse response = approvalService.approve(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/travel-request/{id}/reject")
    @Operation(summary = "Reject a travel request")
    public ResponseEntity<ApiResponse<ApprovalResponse>> reject(
            @PathVariable Long id,
            @Valid @RequestBody RejectTravelRequestRequest request) {
        ApprovalResponse response = approvalService.reject(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/travel-request/{id}/history")
    @Operation(summary = "Get approval history for a travel request")
    public ResponseEntity<ApiResponse<List<ApprovalSummaryResponse>>> findHistory(@PathVariable Long id) {
        List<ApprovalSummaryResponse> response = approvalService.findHistory(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
