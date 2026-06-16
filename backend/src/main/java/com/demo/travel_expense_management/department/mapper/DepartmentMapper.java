package com.demo.travel_expense_management.department.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.demo.travel_expense_management.department.dto.request.CreateDepartmentRequest;
import com.demo.travel_expense_management.department.dto.request.UpdateDepartmentRequest;
import com.demo.travel_expense_management.department.dto.response.DepartmentResponse;
import com.demo.travel_expense_management.department.dto.response.DepartmentSummaryResponse;
import com.demo.travel_expense_management.department.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Department toEntity(CreateDepartmentRequest request);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateDepartmentRequest request, @MappingTarget Department department);

    DepartmentResponse toResponse(Department department);

    DepartmentSummaryResponse toSummaryResponse(Department department);

    List<DepartmentSummaryResponse> toSummaryResponseList(List<Department> departments);
}
