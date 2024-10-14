package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface NotificationService {
    BasicRestResponse getNotifications(final Integer id, final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    void createNotificationForSaveWorkOrder(final WorkOrder workOrder);

    void createNotificationForUpdateWorkOrder(final WorkOrder savedWorkOrder);
}
