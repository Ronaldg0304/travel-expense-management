package com.demo.travel_expense_management.refund.service;

import com.demo.travel_expense_management.refund.dto.request.RegisterRefundRequest;
import com.demo.travel_expense_management.refund.dto.response.RefundResponse;

public interface RefundService {

    RefundResponse registerRefund(Long legalizationId, RegisterRefundRequest request);
}
