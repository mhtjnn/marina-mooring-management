package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuickbookCustomerResponseDto implements Serializable {

    private static final long serialVersionUID = 5526863509L;

    private Integer id;

    private String quickbookCustomerName;

    private String quickbookCustomerId;

    private Integer userId;
}
