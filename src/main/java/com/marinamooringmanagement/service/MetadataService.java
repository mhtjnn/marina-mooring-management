package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface MetadataService {
    BasicRestResponse fetchStatus(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchBoatType(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchSizeOfWeight(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchTypeOfWeight(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchTopChainCondition(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchEyeConditions(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchBottomChainConditions(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchShackleSwivelConditions(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchPennantConditions(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchCustomers(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchBoatyards(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchInventoryType(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchCustomerOwners(BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchMooringsBasedOnCustomerId(final BaseSearchRequest baseSearchRequest, final Integer customerId, final HttpServletRequest request);
}
