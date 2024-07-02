package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface WorkOrderService {
    BasicRestResponse fetchWorkOrders(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    BasicRestResponse saveWorkOrder(final WorkOrderRequestDto workOrderRequestDto, final HttpServletRequest request);

    BasicRestResponse updateWorkOrder(final WorkOrderRequestDto workOrderRequestDto, final Integer workOrderId, final HttpServletRequest request);

    BasicRestResponse deleteWorkOrder(final Integer id, final HttpServletRequest request);

    BasicRestResponse fetchOpenWorkOrders(final BaseSearchRequest baseSearchRequest, final Integer technicianId, final HttpServletRequest request, final String filterDateFrom, final String filterDateTo);

    BasicRestResponse fetchCloseWorkOrders(final BaseSearchRequest baseSearchRequest, final Integer technicianId, final HttpServletRequest request, final String filterDateFrom, final String filterDateTo);

    BasicRestResponse fetchAllOpenWorkOrders(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request, final String filterDateFrom, final String filterDateTo);
}
