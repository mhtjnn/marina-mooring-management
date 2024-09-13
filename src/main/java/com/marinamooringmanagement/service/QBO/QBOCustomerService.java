package com.marinamooringmanagement.service.QBO;

import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpRequest;

public interface QBOCustomerService {
    BasicRestResponse fetchCustomers(HttpServletRequest request);
}
