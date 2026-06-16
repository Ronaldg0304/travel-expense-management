package com.demo.travel_expense_management.approval.service;

import java.util.List;

import com.demo.travel_expense_management.approval.dto.request.ApproveTravelRequestRequest;
import com.demo.travel_expense_management.approval.dto.request.RejectTravelRequestRequest;
import com.demo.travel_expense_management.approval.dto.response.ApprovalResponse;
import com.demo.travel_expense_management.approval.dto.response.ApprovalSummaryResponse;

public interface ApprovalService {

    ApprovalResponse approve(Long travelRequestId, ApproveTravelRequestRequest request);

    ApprovalResponse reject(Long travelRequestId, RejectTravelRequestRequest request);

    List<ApprovalSummaryResponse> findHistory(Long travelRequestId);
}
