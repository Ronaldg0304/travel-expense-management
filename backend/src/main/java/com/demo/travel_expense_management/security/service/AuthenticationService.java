package com.demo.travel_expense_management.security.service;

import com.demo.travel_expense_management.security.dto.request.LoginRequest;
import com.demo.travel_expense_management.security.dto.response.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);
}
