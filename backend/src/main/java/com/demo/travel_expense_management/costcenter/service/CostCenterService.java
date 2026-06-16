package com.demo.travel_expense_management.costcenter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.travel_expense_management.costcenter.dto.request.CreateCostCenterRequest;
import com.demo.travel_expense_management.costcenter.dto.request.UpdateCostCenterRequest;
import com.demo.travel_expense_management.costcenter.dto.response.CostCenterResponse;
import com.demo.travel_expense_management.costcenter.dto.response.CostCenterSummaryResponse;

public interface CostCenterService {

    CostCenterResponse create(CreateCostCenterRequest request);

    CostCenterResponse update(Long id, UpdateCostCenterRequest request);

    CostCenterResponse findById(Long id);

    Page<CostCenterSummaryResponse> findAll(Pageable pageable);

    void activate(Long id);

    void deactivate(Long id);
}
