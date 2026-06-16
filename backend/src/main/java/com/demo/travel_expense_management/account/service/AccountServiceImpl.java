package com.demo.travel_expense_management.account.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.travel_expense_management.account.dto.request.CreateAccountRequest;
import com.demo.travel_expense_management.account.dto.request.UpdateAccountRequest;
import com.demo.travel_expense_management.account.dto.response.AccountResponse;
import com.demo.travel_expense_management.account.dto.response.AccountSummaryResponse;
import com.demo.travel_expense_management.account.entity.Account;
import com.demo.travel_expense_management.account.mapper.AccountMapper;
import com.demo.travel_expense_management.account.repository.AccountRepository;
import com.demo.travel_expense_management.common.exception.BusinessException;
import com.demo.travel_expense_management.common.exception.ResourceNotFoundException;
import com.demo.travel_expense_management.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        userRepository.findById(request.userId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));

        if (accountRepository.existsByUserIdAndAccountTypeAndAccountNumber(
                request.userId(), request.accountType(), request.accountNumber())) {
            throw new BusinessException("Account already exists for this user with the same type and number");
        }

        Account account = accountMapper.toEntity(request);
        account.setActive(request.active());

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountResponse update(Long id, UpdateAccountRequest request) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        accountMapper.updateEntity(request, account);

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponse findById(Long id) {
        return accountRepository.findById(id)
            .map(accountMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    @Override
    public Page<AccountSummaryResponse> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable)
            .map(accountMapper::toSummaryResponse);
    }

    @Override
    public List<AccountSummaryResponse> findByUser(Long userId) {
        return accountMapper.toSummaryResponseList(accountRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public void activate(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        account.setActive(true);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        account.setActive(false);
        accountRepository.save(account);
    }
}
