package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuickbookCustomerResponseDto {

    private Integer id;

    private String quickbookCustomerName;

    private String quickbookCustomerId;

    private Integer userId;
}
