package com.demo.travel_expense_management.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.travel_expense_management.user.dto.request.CreateUserRequest;
import com.demo.travel_expense_management.user.dto.request.UpdateUserRequest;
import com.demo.travel_expense_management.user.dto.response.UserResponse;
import com.demo.travel_expense_management.user.dto.response.UserSummaryResponse;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse update(Long id, UpdateUserRequest request);

    UserResponse findById(Long id);

    Page<UserSummaryResponse> findAll(Pageable pageable);

    void activate(Long id);

    void deactivate(Long id);
}
