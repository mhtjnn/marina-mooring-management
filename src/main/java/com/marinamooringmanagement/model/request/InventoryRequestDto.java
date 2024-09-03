package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestDto {

    private Integer id;

    private Integer inventoryTypeId;

    private String itemName;

    private BigDecimal cost;

    private BigDecimal salePrice;

    private String taxable;

    private Integer quantity;
}
