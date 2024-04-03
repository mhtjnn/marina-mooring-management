package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a vendor entity that maps to the "vendor" table in the database.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vendor")
public class Vendor extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * Name of the vendor.
     */
    @Column(name = "vendor_name")
    private String vendorName;

    /**
     * Slot and date information for the vendor.
     */
    @Column(name = "slot_and_date")
    private String slotAndDate;

    /**
     * Moorings information for the vendor.
     */
    @Column(name = "moorings")
    private String moorings;

    /**
     * Number of users associated with the vendor.
     */
    @Column(name = "no_of_users")
    private Integer noOfUsers;

    /**
     * Inventory price information for the vendor.
     */
    @Column(name = "inventory_price")
    private String inventoryPrice;

    /**
     * Contact information for the vendor.
     */
    @Column(name = "contact")
    private String contact;
}
