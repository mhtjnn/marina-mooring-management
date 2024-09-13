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
            "WHERE v.id = :vendorId")
    List<InventoryMetadataResponse> findAllByVendorIdMetadata(@Param("vendorId") Integer vendorId);
}
