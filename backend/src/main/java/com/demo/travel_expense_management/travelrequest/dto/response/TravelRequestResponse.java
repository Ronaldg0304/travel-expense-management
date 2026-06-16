package com.demo.travel_expense_management.travelrequest.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.demo.travel_expense_management.travelrequest.entity.RequestStatus;

public record TravelRequestResponse(
    Long id,
    String requestNumber,
    Long applicantId,
    String applicantFullName,
    Long departmentId,
    String departmentName,
    String travelPurpose,
    String destination,
    LocalDate departureDate,
    LocalDate returnDate,
    Integer requestedAmount,
    Integer approvedAmount,
    RequestStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
