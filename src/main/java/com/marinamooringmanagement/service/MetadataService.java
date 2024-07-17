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

    BasicRestResponse fetchCustomers(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchBoatyards(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchInventoryType(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchCustomerOwners(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchMooringsBasedOnCustomerId(final BaseSearchRequest baseSearchRequest, final Integer customerId, final HttpServletRequest request);

    BasicRestResponse fetchMooringsBasedOnBoatyardId(final BaseSearchRequest baseSearchRequest, final Integer boatyardId, final HttpServletRequest request);

    BasicRestResponse fetchCustomerBasedOnMooringId(final BaseSearchRequest baseSearchRequest, final  Integer mooringId, final HttpServletRequest request);

    BasicRestResponse fetchBoatyardBasedOnMooringId(final BaseSearchRequest baseSearchRequest, final Integer mooringId, final HttpServletRequest request);

    BasicRestResponse fetchMooringBasedOnCustomerIdAndMooringId(final BaseSearchRequest baseSearchRequest, final Integer customerId, final Integer boatyardId, final HttpServletRequest request);

    BasicRestResponse fetchMooringIds(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchTechnicians(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchWorkOrderStatus(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchCustomerTypes(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchServiceAreaTypes(final BaseSearchRequest baseSearchRequest);

    BasicRestResponse fetchServiceAreas(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);
}
