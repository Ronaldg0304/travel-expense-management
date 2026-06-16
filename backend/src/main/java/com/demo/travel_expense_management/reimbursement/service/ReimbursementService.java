package com.demo.travel_expense_management.reimbursement.service;

import com.demo.travel_expense_management.reimbursement.dto.request.RegisterReimbursementRequest;
import com.demo.travel_expense_management.reimbursement.dto.response.ReimbursementResponse;

public interface ReimbursementService {

    ReimbursementResponse registerReimbursement(Long legalizationId, RegisterReimbursementRequest request);
}
