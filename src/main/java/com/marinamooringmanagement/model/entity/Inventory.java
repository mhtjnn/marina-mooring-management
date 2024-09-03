package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.marinamooringmanagement.model.entity.metadata.InventoryType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory")
public class Inventory extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "taxable")
    private Boolean taxable;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_type_id")
    private InventoryType inventoryType;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    @JsonBackReference
    @ToString.Exclude
    private Vendor vendor;

}
