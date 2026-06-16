package com.demo.travel_expense_management.account.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.demo.travel_expense_management.account.entity.Account;
import com.demo.travel_expense_management.account.entity.AccountType;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    List<Account> findByUserId(Long userId);

    Optional<Account> findByUserIdAndId(Long userId, Long id);

    boolean existsByUserIdAndAccountTypeAndAccountNumber(Long userId, AccountType accountType, String accountNumber);
}
