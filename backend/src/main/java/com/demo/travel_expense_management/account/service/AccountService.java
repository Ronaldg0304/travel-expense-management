package com.demo.travel_expense_management.account.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.travel_expense_management.account.dto.request.CreateAccountRequest;
import com.demo.travel_expense_management.account.dto.request.UpdateAccountRequest;
import com.demo.travel_expense_management.account.dto.response.AccountResponse;
import com.demo.travel_expense_management.account.dto.response.AccountSummaryResponse;

public interface AccountService {

    AccountResponse create(CreateAccountRequest request);

    AccountResponse update(Long id, UpdateAccountRequest request);

    AccountResponse findById(Long id);

    Page<AccountSummaryResponse> findAll(Pageable pageable);

    List<AccountSummaryResponse> findByUser(Long userId);

    void activate(Long id);

    void deactivate(Long id);
}
