package com.demo.travel_expense_management.disbursement.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.demo.travel_expense_management.disbursement.dto.response.DisbursementResponse;
import com.demo.travel_expense_management.disbursement.dto.response.DisbursementSummaryResponse;
import com.demo.travel_expense_management.disbursement.entity.Disbursement;

@Mapper(componentModel = "spring")
public interface DisbursementMapper {

    @Mapping(target = "travelRequestId", source = "travelRequest.id")
    @Mapping(target = "requestNumber", source = "travelRequest.requestNumber")
    @Mapping(target = "applicantName", expression = "java(disbursement.getTravelRequest().getApplicant().getFirstName() + \" \" + disbursement.getTravelRequest().getApplicant().getLastName())")
    @Mapping(target = "approvedAmount", source = "travelRequest.approvedAmount")
    @Mapping(target = "registeredById", source = "registeredBy.id")
    @Mapping(target = "registeredByName", expression = "java(disbursement.getRegisteredBy().getFirstName() + \" \" + disbursement.getRegisteredBy().getLastName())")
    @Mapping(target = "disbursementDate", source = "paymentDate")
    DisbursementResponse toResponse(Disbursement disbursement);

    @Mapping(target = "applicantName", expression = "java(disbursement.getTravelRequest().getApplicant().getFirstName() + \" \" + disbursement.getTravelRequest().getApplicant().getLastName())")
    @Mapping(target = "disbursementDate", source = "paymentDate")
    DisbursementSummaryResponse toSummaryResponse(Disbursement disbursement);

    List<DisbursementSummaryResponse> toSummaryResponseList(List<Disbursement> disbursements);
}
