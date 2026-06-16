package com.demo.travel_expense_management.legalization.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.demo.travel_expense_management.expense.mapper.ExpenseMapper;
import com.demo.travel_expense_management.legalization.dto.response.LegalizationResponse;
import com.demo.travel_expense_management.legalization.dto.response.LegalizationSummaryResponse;
import com.demo.travel_expense_management.legalization.entity.Legalization;

@Mapper(componentModel = "spring", uses = {ExpenseMapper.class})
public interface LegalizationMapper {

    @Mapping(target = "travelRequestId", source = "travelRequest.id")
    @Mapping(target = "requestNumber", source = "travelRequest.requestNumber")
    @Mapping(target = "applicantName", expression = "java(legalization.getTravelRequest().getApplicant().getFirstName() + \" \" + legalization.getTravelRequest().getApplicant().getLastName())")
    @Mapping(target = "costCenterId", source = "costCenter.id")
    @Mapping(target = "costCenterName", source = "costCenter.name")
    @Mapping(target = "totalExpenses", expression = "java(legalization.getExpenses() == null ? 0 : legalization.getExpenses().stream().mapToInt(e -> e.getAmount()).sum())")
    @Mapping(target = "status", source = "travelRequest.status")
    LegalizationResponse toResponse(Legalization legalization);

    @Mapping(target = "requestNumber", source = "travelRequest.requestNumber")
    @Mapping(target = "applicantName", expression = "java(legalization.getTravelRequest().getApplicant().getFirstName() + \" \" + legalization.getTravelRequest().getApplicant().getLastName())")
    @Mapping(target = "totalExpenses", expression = "java(legalization.getExpenses() == null ? 0 : legalization.getExpenses().stream().mapToInt(e -> e.getAmount()).sum())")
    @Mapping(target = "status", source = "travelRequest.status")
    LegalizationSummaryResponse toSummaryResponse(Legalization legalization);

    List<LegalizationSummaryResponse> toSummaryResponseList(List<Legalization> legalizations);
}
