package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.QuickbookCustomerRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface QuickbookCustomerService {
    BasicRestResponse fetchQuickbookCustomers(final BaseSearchRequest baseSearchRequest, final  String searchText, final  HttpServletRequest request);

    BasicRestResponse saveQuickbookCustomer(final QuickbookCustomerRequestDto quickbookCustomer, final HttpServletRequest request);

    BasicRestResponse updateQuickbookCustomer(final QuickbookCustomerRequestDto quickbookCustomerRequestDto, final Integer quickbookCustomerId, final HttpServletRequest request);

    BasicRestResponse deleteQuickbookCustomer(final Integer quickbookCustomerId, final HttpServletRequest request);

    BasicRestResponse mapCustomerToQuickbook(final Integer quickbookCustomerId, final Integer customerId, final HttpServletRequest request);
}
