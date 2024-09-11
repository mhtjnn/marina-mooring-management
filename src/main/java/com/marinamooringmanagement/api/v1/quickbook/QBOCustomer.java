package com.marinamooringmanagement.api.v1.quickbook;

import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.QBO.QBOCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/QBOCustomer")
@Tag(name = "Quickbook Controller", description = "These are API's for quickbook customer.")
public class QBOCustomer {

    @Autowired
    private QBOCustomerService qboCustomerService;

    @Operation(
            summary = "API to fetch quickbook customer from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }
    )
    @GetMapping("/")
    public BasicRestResponse fetchQBOCustomers(HttpSession session) {
        return qboCustomerService.fetchCustomers(session);
    }

}
