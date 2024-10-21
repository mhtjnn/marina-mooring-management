package com.marinamooringmanagement.model.response.metadata;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMetadataResponse implements Serializable {

    private static final long serialVersionUID = 1345786234L;

    private Integer id;

    private String itemName;

    private Integer quantity;
}
