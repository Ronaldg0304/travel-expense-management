package com.demo.travel_expense_management.legalization.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.travel_expense_management.legalization.dto.request.CreateLegalizationRequest;
import com.demo.travel_expense_management.legalization.dto.response.LegalizationResponse;
import com.demo.travel_expense_management.legalization.dto.response.LegalizationSummaryResponse;

public interface LegalizationService {

    LegalizationResponse create(CreateLegalizationRequest request);

    LegalizationResponse findById(Long id);

    LegalizationResponse findByTravelRequest(Long travelRequestId);

    Page<LegalizationSummaryResponse> findAll(Pageable pageable);
}
