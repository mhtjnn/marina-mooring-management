package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {

    @NotNull(message = "Payment Type cannot be blank")
    private Integer paymentTypeId;

    @NotNull(message = "Amount cannot be blank")
    private Double amount;

}
