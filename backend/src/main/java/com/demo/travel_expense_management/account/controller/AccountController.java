package com.demo.travel_expense_management.account.controller;

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

import com.demo.travel_expense_management.account.dto.request.CreateAccountRequest;
import com.demo.travel_expense_management.account.dto.request.UpdateAccountRequest;
import com.demo.travel_expense_management.account.dto.response.AccountResponse;
import com.demo.travel_expense_management.account.dto.response.AccountSummaryResponse;
import com.demo.travel_expense_management.account.service.AccountService;
import com.demo.travel_expense_management.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management API")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<ApiResponse<AccountResponse>> create(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing account")
    public ResponseEntity<ApiResponse<AccountResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccountRequest request) {
        AccountResponse response = accountService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    public ResponseEntity<ApiResponse<AccountResponse>> findById(@PathVariable Long id) {
        AccountResponse response = accountService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of accounts")
    public ResponseEntity<ApiResponse<Page<AccountSummaryResponse>>> findAll(Pageable pageable) {
        Page<AccountSummaryResponse> response = accountService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get accounts by user ID")
    public ResponseEntity<ApiResponse<List<AccountSummaryResponse>>> findByUser(@PathVariable Long userId) {
        List<AccountSummaryResponse> response = accountService.findByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate an account")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Long id) {
        accountService.activate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Account activated", null));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate an account")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        accountService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Account deactivated", null));
    }
}
