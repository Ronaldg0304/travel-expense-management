package com.demo.travel_expense_management.department.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.demo.travel_expense_management.department.dto.request.CreateDepartmentApproverRequest;
import com.demo.travel_expense_management.department.dto.request.UpdateDepartmentApproverRequest;
import com.demo.travel_expense_management.department.dto.response.DepartmentApproverResponse;
import com.demo.travel_expense_management.department.dto.response.DepartmentApproverSummaryResponse;
import com.demo.travel_expense_management.department.entity.DepartmentApprover;

@Mapper(componentModel = "spring")
public interface DepartmentApproverMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "approver", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DepartmentApprover toEntity(CreateDepartmentApproverRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "approver", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateDepartmentApproverRequest request, @MappingTarget DepartmentApprover departmentApprover);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentCode", source = "department.code")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "userId", source = "approver.id")
    @Mapping(target = "userFullName", expression = "java(departmentApprover.getApprover().getFirstName() + \" \" + departmentApprover.getApprover().getLastName())")
    @Mapping(target = "userEmail", source = "approver.email")
    DepartmentApproverResponse toResponse(DepartmentApprover departmentApprover);

    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "userFullName", expression = "java(departmentApprover.getApprover().getFirstName() + \" \" + departmentApprover.getApprover().getLastName())")
    DepartmentApproverSummaryResponse toSummaryResponse(DepartmentApprover departmentApprover);

    List<DepartmentApproverSummaryResponse> toSummaryResponseList(List<DepartmentApprover> departmentApprovers);
}
