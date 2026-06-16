package com.demo.travel_expense_management.department.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.travel_expense_management.department.dto.request.CreateDepartmentApproverRequest;
import com.demo.travel_expense_management.department.dto.request.UpdateDepartmentApproverRequest;
import com.demo.travel_expense_management.department.dto.response.DepartmentApproverResponse;
import com.demo.travel_expense_management.department.dto.response.DepartmentApproverSummaryResponse;

public interface DepartmentApproverService {

    DepartmentApproverResponse create(CreateDepartmentApproverRequest request);

    DepartmentApproverResponse update(Long id, UpdateDepartmentApproverRequest request);

    DepartmentApproverResponse findById(Long id);

    Page<DepartmentApproverSummaryResponse> findAll(Pageable pageable);

    List<DepartmentApproverSummaryResponse> findByDepartment(Long departmentId);

    void activate(Long id);

    void deactivate(Long id);
}
