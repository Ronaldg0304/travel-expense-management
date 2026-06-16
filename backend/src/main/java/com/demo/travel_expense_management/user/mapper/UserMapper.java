package com.demo.travel_expense_management.user.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.demo.travel_expense_management.user.dto.request.CreateUserRequest;
import com.demo.travel_expense_management.user.dto.request.UpdateUserRequest;
import com.demo.travel_expense_management.user.dto.response.UserResponse;
import com.demo.travel_expense_management.user.dto.response.UserSummaryResponse;
import com.demo.travel_expense_management.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "department", ignore = true)
    User toEntity(CreateUserRequest request);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    UserResponse toResponse(User user);

    @Mapping(target = "departmentName", source = "department.name")
    UserSummaryResponse toSummaryResponse(User user);

    List<UserSummaryResponse> toSummaryResponseList(List<User> users);
}
