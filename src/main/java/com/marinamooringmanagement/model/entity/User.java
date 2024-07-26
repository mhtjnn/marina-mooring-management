package com.marinamooringmanagement.model.entity;

import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a User.
 * This class inherits common fields from the Base class and includes user-specific attributes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_user")
public class User extends Base {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The first name of the user.
     */
    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    /**
     * The email address of the user.
     */
    @Column(name = "email")
    private String email;

    /**
     * The phone number of the user.
     */
    @Column(name = "phoneNumber")
    private String phoneNumber;

    /**
     * The password of the user.
     */
    @Column(name = "password")
    private String password;

    /**
     * The ID of the customer admin associated with the user.
     */
    @Column(name = "customer_owner_ID")
    private Integer customerOwnerId;

    @Column(name = "company_name")
    private String companyName;

    /**
     * The street address of the user.
     */
    @Column(name = "address")
    private String address;

    /**
     * The zip code of the user's location.
     */
    @Column(name = "zipcode")
    private String zipCode;

    /**
     * The role associated with the user.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    /**
     * The state associated with the user.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private State state;

    /**
     * The country associated with the user.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;
}

