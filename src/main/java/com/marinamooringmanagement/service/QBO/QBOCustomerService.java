package com.marinamooringmanagement.service.QBO;

import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpSession;

public interface QBOCustomerService {
    BasicRestResponse fetchCustomers(final HttpSession session);
}
