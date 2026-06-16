package com.demo.travel_expense_management.travelrequest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.demo.travel_expense_management.travelrequest.dto.request.CreateTravelRequestRequest;
import com.demo.travel_expense_management.travelrequest.dto.request.UpdateTravelRequestRequest;
import com.demo.travel_expense_management.travelrequest.dto.response.TravelRequestResponse;
import com.demo.travel_expense_management.travelrequest.dto.response.TravelRequestSummaryResponse;
import com.demo.travel_expense_management.travelrequest.entity.TravelRequest;

@Mapper(componentModel = "spring")
public interface TravelRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestNumber", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "observations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "purpose", source = "travelPurpose")
    TravelRequest toEntity(CreateTravelRequestRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestNumber", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "observations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "purpose", source = "travelPurpose")
    void updateEntity(UpdateTravelRequestRequest request, @MappingTarget TravelRequest travelRequest);

    @Mapping(target = "approvedAmount", ignore = true)
    @Mapping(target = "applicantId", source = "applicant.id")
    @Mapping(target = "applicantFullName", expression = "java(travelRequest.getApplicant().getFirstName() + \" \" + travelRequest.getApplicant().getLastName())")
    @Mapping(target = "departmentId", source = "applicant.department.id")
    @Mapping(target = "departmentName", source = "applicant.department.name")
    @Mapping(target = "travelPurpose", source = "purpose")
    TravelRequestResponse toResponse(TravelRequest travelRequest);

    @Mapping(target = "applicantFullName", expression = "java(travelRequest.getApplicant().getFirstName() + \" \" + travelRequest.getApplicant().getLastName())")
    TravelRequestSummaryResponse toSummaryResponse(TravelRequest travelRequest);

    List<TravelRequestSummaryResponse> toSummaryResponseList(List<TravelRequest> travelRequests);
}
