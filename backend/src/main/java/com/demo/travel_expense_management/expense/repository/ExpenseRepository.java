package com.demo.travel_expense_management.expense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.travel_expense_management.expense.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByLegalizationId(Long legalizationId);
}
