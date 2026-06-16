package com.demo.travel_expense_management.expense.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.demo.travel_expense_management.expense.entity.ExpenseType;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long>, JpaSpecificationExecutor<ExpenseType> {

    Optional<ExpenseType> findByCode(String code);

    boolean existsByCode(String code);
}
