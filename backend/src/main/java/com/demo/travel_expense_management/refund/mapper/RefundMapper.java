package com.demo.travel_expense_management.refund.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.demo.travel_expense_management.refund.dto.response.RefundResponse;
import com.demo.travel_expense_management.refund.entity.Refund;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    @Mapping(target = "legalizationId", source = "legalization.id")
    @Mapping(target = "requestNumber", expression = "java(refund.getLegalization().getTravelRequest().getRequestNumber())")
    @Mapping(target = "refundAmount", source = "amount")
    @Mapping(target = "refundReference", source = "referenceNumber")
    @Mapping(target = "refundDate", source = "paymentDate")
    @Mapping(target = "comments", source = "observations")
    @Mapping(target = "registeredById", source = "registeredBy.id")
    @Mapping(target = "registeredByName", expression = "java(refund.getRegisteredBy().getFirstName() + \" \" + refund.getRegisteredBy().getLastName())")
    RefundResponse toResponse(Refund refund);
}
