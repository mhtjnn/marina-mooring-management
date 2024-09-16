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

    @Column(name = "parent_inventory_id")
    private Integer parentInventoryId;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_type_id")
    private InventoryType inventoryType;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    @JsonBackReference
    @ToString.Exclude
    private Vendor vendor;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    @JsonBackReference
    @ToString.Exclude
    private WorkOrder workOrder;

    Inventory(Integer id, String itemName, Integer quantity, Integer parentInventoryId, Integer vendorId, Integer workOrderId) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.parentInventoryId = parentInventoryId;
        this.vendor = Vendor.builder().id(vendorId).build();
        this.workOrder = WorkOrder.builder().id(workOrderId).build();
    }

}
