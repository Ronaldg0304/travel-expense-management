package com.demo.travel_expense_management.refund.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.travel_expense_management.common.response.ApiResponse;
import com.demo.travel_expense_management.refund.dto.request.RegisterRefundRequest;
import com.demo.travel_expense_management.refund.dto.response.RefundResponse;
import com.demo.travel_expense_management.refund.service.RefundService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
@Tag(name = "Refunds", description = "Refund management API")
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/legalization/{id}")
    @Operation(summary = "Register a refund for a legalization")
    public ResponseEntity<ApiResponse<RefundResponse>> registerRefund(
            @PathVariable Long id,
            @Valid @RequestBody RegisterRefundRequest request) {
        RefundResponse response = refundService.registerRefund(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
