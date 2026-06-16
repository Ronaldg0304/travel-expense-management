package com.demo.travel_expense_management.reimbursement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.demo.travel_expense_management.reimbursement.dto.response.ReimbursementResponse;
import com.demo.travel_expense_management.reimbursement.entity.Reimbursement;

@Mapper(componentModel = "spring")
public interface ReimbursementMapper {

    @Mapping(target = "legalizationId", source = "legalization.id")
    @Mapping(target = "requestNumber", expression = "java(reimbursement.getLegalization().getTravelRequest().getRequestNumber())")
    @Mapping(target = "reimbursementAmount", source = "amount")
    @Mapping(target = "accountId", source = "employeeAccount.id")
    @Mapping(target = "accountNumber", source = "employeeAccount.accountNumber")
    @Mapping(target = "paymentReference", source = "referenceNumber")
    @Mapping(target = "comments", source = "observations")
    @Mapping(target = "registeredById", source = "registeredBy.id")
    @Mapping(target = "registeredByName", expression = "java(reimbursement.getRegisteredBy().getFirstName() + \" \" + reimbursement.getRegisteredBy().getLastName())")
    ReimbursementResponse toResponse(Reimbursement reimbursement);
}
