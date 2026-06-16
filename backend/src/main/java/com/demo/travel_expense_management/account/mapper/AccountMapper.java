package com.demo.travel_expense_management.account.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.demo.travel_expense_management.account.dto.request.CreateAccountRequest;
import com.demo.travel_expense_management.account.dto.request.UpdateAccountRequest;
import com.demo.travel_expense_management.account.dto.response.AccountResponse;
import com.demo.travel_expense_management.account.dto.response.AccountSummaryResponse;
import com.demo.travel_expense_management.account.entity.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Account toEntity(CreateAccountRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accountType", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateAccountRequest request, @MappingTarget Account account);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(account.getUser().getFirstName() + \" \" + account.getUser().getLastName())")
    AccountResponse toResponse(Account account);

    AccountSummaryResponse toSummaryResponse(Account account);

    List<AccountSummaryResponse> toSummaryResponseList(List<Account> accounts);
}
