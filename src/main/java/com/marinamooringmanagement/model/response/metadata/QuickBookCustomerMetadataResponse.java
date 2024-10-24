package com.marinamooringmanagement.model.response.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuickBookCustomerMetadataResponse implements Serializable {

    private static final long serialVersionUID = 55268635079234L;

    private Integer id;

    private String quickbookCustomerFirstName;

    private String quickbookCustomerLastName;

    private String quickbookCustomerId;

}
