package com.demo.travel_expense_management.travelrequest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.travel_expense_management.travelrequest.dto.request.CreateTravelRequestRequest;
import com.demo.travel_expense_management.travelrequest.dto.request.UpdateTravelRequestRequest;
import com.demo.travel_expense_management.travelrequest.dto.response.TravelRequestResponse;
import com.demo.travel_expense_management.travelrequest.dto.response.TravelRequestSummaryResponse;

public interface TravelRequestService {

    TravelRequestResponse createDraft(CreateTravelRequestRequest request);

    TravelRequestResponse updateDraft(Long id, UpdateTravelRequestRequest request);

    TravelRequestResponse findById(Long id);

    Page<TravelRequestSummaryResponse> findAll(Pageable pageable);

    Page<TravelRequestSummaryResponse> findMyRequests(Pageable pageable);

    TravelRequestResponse submitForApproval(Long id);

    TravelRequestResponse cancelDraft(Long id);
}
