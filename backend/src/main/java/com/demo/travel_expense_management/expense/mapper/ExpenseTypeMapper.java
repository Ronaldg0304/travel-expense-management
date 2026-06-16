package com.demo.travel_expense_management.expense.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.demo.travel_expense_management.expense.dto.request.CreateExpenseTypeRequest;
import com.demo.travel_expense_management.expense.dto.request.UpdateExpenseTypeRequest;
import com.demo.travel_expense_management.expense.dto.response.ExpenseTypeResponse;
import com.demo.travel_expense_management.expense.dto.response.ExpenseTypeSummaryResponse;
import com.demo.travel_expense_management.expense.entity.ExpenseType;

@Mapper(componentModel = "spring")
public interface ExpenseTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ExpenseType toEntity(CreateExpenseTypeRequest request);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateExpenseTypeRequest request, @MappingTarget ExpenseType expenseType);

    ExpenseTypeResponse toResponse(ExpenseType expenseType);

    ExpenseTypeSummaryResponse toSummaryResponse(ExpenseType expenseType);

    List<ExpenseTypeSummaryResponse> toSummaryResponseList(List<ExpenseType> expenseTypes);
}
