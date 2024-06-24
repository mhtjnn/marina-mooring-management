package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

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

    @Column(name = "note")
    private String note;
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

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_type_id")
    private CustomerType customerType;

    @OneToMany(mappedBy = "customer", cascade = {}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Mooring> mooringList;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_image_id")
    private List<Image> imageList;
}
