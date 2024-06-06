package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * Entity class representing a Customer.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "customer")
public class Customer extends Base {
    /**
     * The unique identifier of the customer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * The name of the customer.
     */
    @Column(name = "customer_name")
    private String customerName;
    /**
     * The customer ID.
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * The phone number of the customer.
     */
    @Column(name = "phone")
    @Pattern(regexp = "^(\\d[-. ]?){10}$", message = "Invalid phone number format")
    private String phone;

    /**
     * The email address of the customer.
     */
    @Column(name = "email_address")
    private String emailAddress;

    /**
     * The street and house number of the customer's address.
     */
    @Column(name = "street_house")
    private String streetHouse;

    /**
     * The Apt/Suite of the customer's address.
     */
    @Column(name = "apt_suite")
    private String aptSuite;

    /**
     * The state of the customer's address.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private State state;
    /**
     * The country of the customer's address.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;
    /**
     * The zip code of the customer's address.
     */
    @Column(name = "zipcode")
    private String zipCode;

    @OneToMany(mappedBy = "customer", cascade = {}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Mooring> mooringList;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
