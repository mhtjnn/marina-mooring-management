package com.marinamooringmanagement.api.v1.report;

import com.marinamooringmanagement.constants.Authority;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.ReportService;
import com.marinamooringmanagement.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/report")
@CrossOrigin
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private WorkOrderService workOrderService;

    @GetMapping(value = "/jobType", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "API to get job type report in the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }
    )
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse getReportByJobType(final HttpServletRequest request) {
        return reportService.getReportByJobType(request);
    }

    @GetMapping("/jobLocation")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "API to get job location report in the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }
    )
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse getReportByJobLocation(final HttpServletRequest request) {
        return reportService.getReportByJobLocation(request);
    }

}
