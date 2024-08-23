package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface ReportService {

    BasicRestResponse getReportByJobType(final HttpServletRequest request);

    BasicRestResponse getReportByJobLocation(final HttpServletRequest request);
}
