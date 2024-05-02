package com.marinamooringmanagement.model.entity;

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
@Table(name = "user")
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
    @Column(name = "name")
    private String name;

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

    @Column(name = "user_ID")
    private String userID;

    @Column(name = "customer_admin_ID")
    private String customerAdminId;

    /**
     * The role associated with the user.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

}

