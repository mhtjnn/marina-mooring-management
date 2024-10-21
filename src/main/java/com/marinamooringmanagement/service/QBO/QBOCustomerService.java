package com.marinamooringmanagement.service.QBO;

import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface QBOCustomerService {
    BasicRestResponse fetchCustomers(HttpServletRequest request);
}
