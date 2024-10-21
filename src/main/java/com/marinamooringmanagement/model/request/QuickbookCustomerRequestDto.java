package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuickbookCustomerRequestDto {

    private Integer id;

    private String quickbookCustomerName;

    private String quickbookCustomerId;

}
