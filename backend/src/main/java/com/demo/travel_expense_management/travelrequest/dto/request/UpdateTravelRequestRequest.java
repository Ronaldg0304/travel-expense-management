package com.demo.travel_expense_management.travelrequest.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateTravelRequestRequest(
    @NotBlank @Size(max = 500) String travelPurpose,
    @NotBlank @Size(max = 200) String destination,
    @NotNull LocalDate departureDate,
    @NotNull LocalDate returnDate,
    @NotNull @Positive Integer requestedAmount
) {}
