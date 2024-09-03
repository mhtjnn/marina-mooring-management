package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.InventoryTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponseDto implements Serializable {

    private static final long serialVersionUID = 5526863503479L;

    private Integer id;

    private String itemName;

    private BigDecimal cost;

    private BigDecimal salePrice;

    private String taxable;

    private Integer quantity;

    private InventoryTypeDto inventoryType;

    private VendorResponseDto vendorResponseDto;
}
