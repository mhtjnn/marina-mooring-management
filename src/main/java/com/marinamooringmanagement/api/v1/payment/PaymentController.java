package com.marinamooringmanagement.api.v1.payment;

import com.marinamooringmanagement.constants.Authority;
import com.marinamooringmanagement.model.request.BoatyardRequestDto;
import com.marinamooringmanagement.model.request.PaymentRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/payment")
@Validated
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "/{workOrderInvoiceId}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            tags = "Save payment in the database",
            description = "API to save payment in the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.FINANCE)
    public BasicRestResponse savePayment(
            final @Valid @RequestBody PaymentRequestDto paymentRequestDto,
            final @PathVariable("workOrderInvoiceId") Integer workOrderInvoiceId,
            final HttpServletRequest request
    ) {
        return paymentService.savePayment(paymentRequestDto, request, workOrderInvoiceId);
    }

}
