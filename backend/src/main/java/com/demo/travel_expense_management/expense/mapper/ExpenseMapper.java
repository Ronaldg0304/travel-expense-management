package com.demo.travel_expense_management.expense.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.demo.travel_expense_management.expense.dto.response.ExpenseResponse;
import com.demo.travel_expense_management.expense.entity.Expense;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "expenseTypeId", source = "expenseType.id")
    @Mapping(target = "expenseTypeName", source = "expenseType.name")
    ExpenseResponse toResponse(Expense expense);

    List<ExpenseResponse> toResponseList(List<Expense> expenses);
}
