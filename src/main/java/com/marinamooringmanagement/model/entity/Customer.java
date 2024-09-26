package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.CustomerType;
import com.marinamooringmanagement.model.entity.metadata.State;
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
    private String phone;

    /**
     * The email address of the customer.
     */
    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "notes")
    private String notes;
    /**
     * The street and house number of the customer's address.
     */
    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Image> imageList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "customer", fetch = FetchType.LAZY)
    @JsonManagedReference
    private QuickbookCustomer quickBookCustomer;

    public Customer(
            Integer id, String firstName, String lastName, String customerId, String address, String city, String notes, String emailAddress,
                    Integer stateId, String stateName, Integer countryId, String countryName, String zipCode, Integer customerTypeId,
                    String customerTypeName, Integer userId, String userFirstName, String userSecondName, String phone,
                    Integer roleId, String roleName,
                    Integer quickBookCustomerId, String quickbookCustomerFirstName, String quickbookCustomerLastName, String quickbookCustomerIdStr
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerId = customerId;
        this.address = address;
        this.city = city;
        this.notes = notes;
        this.emailAddress = emailAddress;
        this.state = State.builder().id(stateId).name(stateName).build();
        this.country = Country.builder().id(countryId).name(countryName).build();
        this.zipCode = zipCode;
        this.customerType = CustomerType.builder().id(customerTypeId).type(customerTypeName).build();
        this.phone = phone;
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userSecondName).role(Role.builder().id(roleId).name(roleName).build()).build();
        this.quickBookCustomer = QuickbookCustomer.builder().id(quickBookCustomerId).quickbookCustomerFirstName(quickbookCustomerFirstName).quickbookCustomerLastName(quickbookCustomerLastName).quickbookCustomerId(quickbookCustomerIdStr).build();
    }

    public Customer(
            Integer id, String firstName, String lastName, String customerId, String address, String city, String notes, String emailAddress,
            Integer stateId, String stateName, Integer countryId, String countryName, String zipCode, Integer customerTypeId,
            String customerTypeName, Integer userId, String userFirstName, String userSecondName, String phone,
            Integer roleId, String roleName
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerId = customerId;
        this.address = address;
        this.city = city;
        this.notes = notes;
        this.emailAddress = emailAddress;
        this.state = State.builder().id(stateId).name(stateName).build();
        this.country = Country.builder().id(countryId).name(countryName).build();
        this.zipCode = zipCode;
        this.customerType = CustomerType.builder().id(customerTypeId).type(customerTypeName).build();
        this.phone = phone;
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userSecondName).role(Role.builder().id(roleId).name(roleName).build()).build();
    }

    public Customer(
            Integer id, String firstName, String lastName, String customerId, String address, String city, String notes, String emailAddress,
            Integer stateId, String stateName, Integer countryId, String countryName, String zipCode, Integer customerTypeId,
            String customerTypeName, Integer userId, String userFirstName, String userSecondName, String phone,
            Integer roleId, String roleName, List<Image> imageList
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerId = customerId;
        this.address = address;
        this.city = city;
        this.notes = notes;
        this.emailAddress = emailAddress;
        this.state = State.builder().id(stateId).name(stateName).build();
        this.country = Country.builder().id(countryId).name(countryName).build();
        this.zipCode = zipCode;
        this.customerType = CustomerType.builder().id(customerTypeId).type(customerTypeName).build();
        this.phone = phone;
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userSecondName).role(Role.builder().id(roleId).name(roleName).build()).build();
        this.imageList = imageList;
    }

    public Customer(
            Integer id, String firstName, String lastName
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public Customer(
            Integer id, String firstName, String lastName, String customerId, Integer userId, String userFirstName, String userLastName
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerId = customerId;
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build();
    }

    public Customer(
            Integer id, String firstName, String lastName, Integer userId, String userFirstName, String userLastName
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build();
    }

}
