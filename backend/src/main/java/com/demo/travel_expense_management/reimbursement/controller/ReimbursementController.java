package com.demo.travel_expense_management.reimbursement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.travel_expense_management.common.response.ApiResponse;
import com.demo.travel_expense_management.reimbursement.dto.request.RegisterReimbursementRequest;
import com.demo.travel_expense_management.reimbursement.dto.response.ReimbursementResponse;
import com.demo.travel_expense_management.reimbursement.service.ReimbursementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reimbursements")
@RequiredArgsConstructor
@Tag(name = "Reimbursements", description = "Reimbursement management API")
public class ReimbursementController {

    private final ReimbursementService reimbursementService;

    @PostMapping("/legalization/{id}")
    @Operation(summary = "Register a reimbursement for a legalization")
    public ResponseEntity<ApiResponse<ReimbursementResponse>> registerReimbursement(
            @PathVariable Long id,
            @Valid @RequestBody RegisterReimbursementRequest request) {
        ReimbursementResponse response = reimbursementService.registerReimbursement(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
