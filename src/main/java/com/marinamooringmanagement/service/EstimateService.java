package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.EstimateRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface EstimateService {

    BasicRestResponse fetchEstimates(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    BasicRestResponse saveEstimate(final EstimateRequestDto workOrderRequestDto, final HttpServletRequest request);

    BasicRestResponse updateEstimate(final EstimateRequestDto workOrderRequestDto, final Integer workOrderId, final HttpServletRequest request);

    BasicRestResponse deleteEstimate(final Integer id, final HttpServletRequest request);
}
