package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.PaymentRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    BasicRestResponse savePayment(final PaymentRequestDto paymentRequestDto, final HttpServletRequest request, final Integer workOrderInvoiceId);
}
