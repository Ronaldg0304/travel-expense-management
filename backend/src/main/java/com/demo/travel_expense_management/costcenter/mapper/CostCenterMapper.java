package com.demo.travel_expense_management.costcenter.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.demo.travel_expense_management.costcenter.dto.request.CreateCostCenterRequest;
import com.demo.travel_expense_management.costcenter.dto.request.UpdateCostCenterRequest;
import com.demo.travel_expense_management.costcenter.dto.response.CostCenterResponse;
import com.demo.travel_expense_management.costcenter.dto.response.CostCenterSummaryResponse;
import com.demo.travel_expense_management.costcenter.entity.CostCenter;

@Mapper(componentModel = "spring")
public interface CostCenterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CostCenter toEntity(CreateCostCenterRequest request);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateCostCenterRequest request, @MappingTarget CostCenter costCenter);

    CostCenterResponse toResponse(CostCenter costCenter);

    CostCenterSummaryResponse toSummaryResponse(CostCenter costCenter);

    List<CostCenterSummaryResponse> toSummaryResponseList(List<CostCenter> costCenters);
}
