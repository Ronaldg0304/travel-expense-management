package com.demo.travel_expense_management.travelrequest.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.demo.travel_expense_management.travelrequest.entity.RequestStatus;
import com.demo.travel_expense_management.travelrequest.entity.TravelRequest;

public interface TravelRequestRepository extends JpaRepository<TravelRequest, Long>, JpaSpecificationExecutor<TravelRequest> {

    Optional<TravelRequest> findByRequestNumber(String requestNumber);

    boolean existsByRequestNumber(String requestNumber);

    Page<TravelRequest> findByApplicantId(Long applicantId, Pageable pageable);

    Page<TravelRequest> findByStatus(RequestStatus status, Pageable pageable);

    Page<TravelRequest> findByApplicantIdAndStatus(Long applicantId, RequestStatus status, Pageable pageable);
}
