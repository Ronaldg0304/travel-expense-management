package com.demo.travel_expense_management.travelrequest.dto.response;

import java.time.LocalDate;

import com.demo.travel_expense_management.travelrequest.entity.RequestStatus;

public record TravelRequestSummaryResponse(
    Long id,
    String requestNumber,
    String applicantFullName,
    String destination,
    Integer requestedAmount,
    RequestStatus status,
    LocalDate departureDate,
    LocalDate returnDate
) {}
