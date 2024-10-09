package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface WorkOrderService {
    BasicRestResponse fetchWorkOrders(final BaseSearchRequest baseSearchRequest, final String searchText, final String showCompletedWorkOrders, final HttpServletRequest request);

    BasicRestResponse saveWorkOrder(final WorkOrderRequestDto workOrderRequestDto, final HttpServletRequest request);

    BasicRestResponse updateWorkOrder(final WorkOrderRequestDto workOrderRequestDto, final Integer workOrderId, final HttpServletRequest request);

    BasicRestResponse deleteWorkOrder(final Integer id, final HttpServletRequest request);

    BasicRestResponse fetchOpenWorkOrders(final BaseSearchRequest baseSearchRequest, final Integer technicianId, final HttpServletRequest request, final String filterDateFrom, final String filterDateTo);

    BasicRestResponse fetchCloseWorkOrders(final BaseSearchRequest baseSearchRequest, final Integer technicianId, final HttpServletRequest request, final String filterDateFrom, final String filterDateTo);

    BasicRestResponse fetchAllOpenWorkOrdersAndMooringDueForService(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request);

    BasicRestResponse fetchCompletedWorkOrdersWithPendingPayApproval(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request, final String payStatus);

    BasicRestResponse approveWorkOrder(final Integer id, final HttpServletRequest request, final BigDecimal invoiceAmount);

    BasicRestResponse denyWorkOrder(final Integer id, final HttpServletRequest request, final String reportProblem);

    BasicRestResponse fetchWorkOrderInvoice(BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request);

    BasicRestResponse fetchMooringDueForServiceForTechnician(final Integer id, final HttpServletRequest request);
}
