package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entity class representing a Customer.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Customer {
    /**
     * The unique identifier of the customer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * The name of the customer.
     */
    @Column(name = "CustomerName")
    private String customerName;
    /**
     * The customer ID.
     */
    @Column(name = "CustomerId")
    private String customerId;

    /**
     * The phone number of the customer.
     */
    @Column(name="Phone")
    private String phone;

    /**
     * The email address of the customer.
     */
    @Column(name="EmailAddress")
    private String emailAddress;

    /**
     * The street and house number of the customer's address.
     */
    @Column(name="Street/house")
    private String streetHouse;

    /**
     * The sector or block of the customer's address.
     */
    @Column(name="Sector/Block")
    private String sectorBlock;
    /**
     * The state of the customer's address.
     */
    @Column(name="State")
    private String state;
    /**
     * The country of the customer's address.
     */
    @Column(name="Country")
    private String country;
    /**
     * The pin code of the customer's address.
     */
    @Column(name="Pincode")
    private String pinCode;
    /**
     * Additional notes or comments about the customer.
     */
    @Column(name="Note")
    private String note;



}
