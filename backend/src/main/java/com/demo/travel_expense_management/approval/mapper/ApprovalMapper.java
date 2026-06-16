package com.demo.travel_expense_management.approval.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.demo.travel_expense_management.approval.dto.response.ApprovalResponse;
import com.demo.travel_expense_management.approval.dto.response.ApprovalSummaryResponse;
import com.demo.travel_expense_management.approval.entity.Approval;

@Mapper(componentModel = "spring")
public interface ApprovalMapper {

    @Mapping(target = "travelRequestId", source = "travelRequest.id")
    @Mapping(target = "approverId", source = "approver.id")
    @Mapping(target = "approverName", expression = "java(approval.getApprover().getFirstName() + \" \" + approval.getApprover().getLastName())")
    @Mapping(target = "approvedAmount", source = "travelRequest.approvedAmount")
    ApprovalResponse toResponse(Approval approval);

    @Mapping(target = "approverName", expression = "java(approval.getApprover().getFirstName() + \" \" + approval.getApprover().getLastName())")
    ApprovalSummaryResponse toSummaryResponse(Approval approval);

    List<ApprovalSummaryResponse> toSummaryResponseList(List<Approval> approvals);
}
