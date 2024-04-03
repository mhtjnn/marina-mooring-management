package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponseDto {

    private Integer id;

    private String vendorName;

    private String slotAndDate;

    private String moorings;

    private Integer noOfUsers;

    private String inventoryPrice;

    private String contact;
}
