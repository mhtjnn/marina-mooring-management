package com.marinamooringmanagement.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory_type")
public class InventoryType extends Base{

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

}
