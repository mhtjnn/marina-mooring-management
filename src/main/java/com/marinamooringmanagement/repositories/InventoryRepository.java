package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Inventory;
import com.marinamooringmanagement.model.response.metadata.InventoryMetadataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Page<Inventory> findAll(final Specification<Inventory> spec, final Pageable p);

    List<Inventory> findAll(final Specification<Inventory> spec);

    @Query("SELECT new com.marinamooringmanagement.model.response.metadata.InventoryMetadataResponse(" +
            "i.id, i.itemName, i.quantity) " +
            "FROM Inventory i " +
            "LEFT JOIN i.vendor v " +
            "LEFT JOIN i.workOrder w " +
            "WHERE v.id = :vendorId AND w.id IS NULL AND i.quantity > 0")
    List<InventoryMetadataResponse> findAllByVendorIdMetadata(@Param("vendorId") Integer vendorId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Inventory(" +
            "i.id, i.itemName, i.quantity, i.parentInventoryId, v.id, v.vendorName, w.id) " +
            "FROM Inventory i " +
            "LEFT JOIN i.vendor v " +
            "LEFT JOIN i.workOrder w " +
            "WHERE w.id = :workOrderId")
    List<Inventory> findInventoriesByWorkOrder(@Param("workOrderId") Integer workOrderId);
}
