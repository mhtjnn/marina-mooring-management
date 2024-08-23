package com.marinamooringmanagement.api.v1.report;

import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.ReportService;
import com.marinamooringmanagement.service.WorkOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/report")
@CrossOrigin
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private WorkOrderService workOrderService;

    @GetMapping("/jobType")
    public BasicRestResponse getReportByJobType(final HttpServletRequest request) {
        return reportService.getReportByJobType(request);
    }

    @GetMapping("/jobLocation")
    public BasicRestResponse getReportByJobLocation(final HttpServletRequest request) {
        return reportService.getReportByJobLocation(request);
    }

}
