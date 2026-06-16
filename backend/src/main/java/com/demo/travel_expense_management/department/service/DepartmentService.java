package com.demo.travel_expense_management.department.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.travel_expense_management.department.dto.request.CreateDepartmentRequest;
import com.demo.travel_expense_management.department.dto.request.UpdateDepartmentRequest;
import com.demo.travel_expense_management.department.dto.response.DepartmentResponse;
import com.demo.travel_expense_management.department.dto.response.DepartmentSummaryResponse;

public interface DepartmentService {

    DepartmentResponse create(CreateDepartmentRequest request);

    DepartmentResponse update(Long id, UpdateDepartmentRequest request);

    DepartmentResponse findById(Long id);

    Page<DepartmentSummaryResponse> findAll(Pageable pageable);

    void activate(Long id);

    void deactivate(Long id);
}
