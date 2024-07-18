package com.marinamooringmanagement.model.response.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuickBookCustomerMetadataResponse {

    private Integer id;

    private String quickbookCustomerName;

    private String quickbookCustomerId;

}
