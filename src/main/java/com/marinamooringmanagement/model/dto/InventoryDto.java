package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.dto.metadata.InventoryTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto extends BaseDto{

    private Integer id;

    private String itemName;

    private BigDecimal cost;

    private BigDecimal salePrice;

    private Boolean taxable;

    private Integer quantity;

    private InventoryTypeDto inventoryTypeDto;

    private VendorDto vendorDto;

}
