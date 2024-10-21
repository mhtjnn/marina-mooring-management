package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.State;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private Image image;

    public User(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(Integer id, String firstName, String lastName, String email,
                String phoneNumber, String address, String zipCode,
                Integer roleId, String roleName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.zipCode = zipCode;
        this.role = Role.builder().id(roleId).name(roleName).build();
    }

    public User(Integer id, String firstName, String lastName, String email,
                String phoneNumber, String address, String zipCode,
                Integer customerOwnerId, String companyName,
                Integer stateId, String stateName,
                Integer countryId, String countryName,
                Integer roleId, String roleName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.customerOwnerId = customerOwnerId;
        this.companyName = companyName;
        this.address = address;
        this.zipCode = zipCode;
        this.state = State.builder().id(stateId).name(stateName).build();
        this.country = Country.builder().id(countryId).name(countryName).build();
        this.role = Role.builder().id(roleId).name(roleName).build();
    }
    public User(Integer id, String firstName, String lastName, String email,
                String password, String phoneNumber, String address, String zipCode,
                Integer roleId, String roleName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.zipCode = zipCode;
        this.role = Role.builder().id(roleId).name(roleName).build();
    }

    public User(Integer id, String firstName, String lastName, String email,
                String password,
                Integer roleId, String roleName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = Role.builder().id(roleId).name(roleName).build();
    }

    public User(Integer id, String firstName, String lastName, String email, String password,
                String phoneNumber, Integer customerOwnerId, String companyName, String address,
                String zipCode, Integer roleId, String roleName,
                Integer stateId, String stateName,
                Integer countryId, String countryName,
                Integer imageId, byte[] imageData) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.zipCode = zipCode;
        this.password = password;
        this.companyName = companyName;
        this.customerOwnerId = customerOwnerId;
        this.state = State.builder().id(stateId).name(stateName).build();
        this.country = Country.builder().id(countryId).name(countryName).build();
        this.role = Role.builder().id(roleId).name(roleName).build();
        this.image = Image.builder().id(imageId).imageData(imageData).build();
    }

    public User(Integer id, String firstName, String lastName, String email,
                String phoneNumber, Integer customerOwnerId, String companyName, String address,
                String zipCode,
                Integer roleId, String roleName,
                Integer stateId, String stateName,
                Integer countryId, String countryName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.zipCode = zipCode;
        this.companyName = companyName;
        this.customerOwnerId = customerOwnerId;
        this.state = State.builder().id(stateId).name(stateName).build();
        this.country = Country.builder().id(countryId).name(countryName).build();
        this.role = Role.builder().id(roleId).name(roleName).build();
    }

    public User(Integer id, String firstName, String lastName, String email, String password,
                Integer roleId, String roleName,
                Integer imageId, byte[] imageData) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = Role.builder().id(roleId).name(roleName).build();
        this.image = Image.builder().id(imageId).imageData(imageData).build();
    }

    public User(Integer id,
                Integer imageId, byte[] imageData) {
        this.id = id;
        this.image = Image.builder().id(imageId).imageData(imageData).build();
    }
}

