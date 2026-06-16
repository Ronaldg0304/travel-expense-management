package com.demo.travel_expense_management.disbursement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.travel_expense_management.disbursement.dto.request.RegisterDisbursementRequest;
import com.demo.travel_expense_management.disbursement.dto.response.DisbursementResponse;
import com.demo.travel_expense_management.disbursement.dto.response.DisbursementSummaryResponse;

public interface DisbursementService {

    DisbursementResponse registerDisbursement(Long travelRequestId, RegisterDisbursementRequest request);

    DisbursementResponse findByTravelRequest(Long travelRequestId);

    Page<DisbursementSummaryResponse> findAll(Pageable pageable);
}
