package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Column(name = "vendor_name")
    private String vendorName;

    /**
     * Phone number of the company associated with the vendor.
     */
    @Column(name = "phone_number")
    private String companyPhoneNumber;

    /**
     * Website URL of the company associated with the vendor.
     */
    @Column(name = "website")
    private String website;

    /**
     * Street address of the vendor.
     */
    @Column(name = "address")
    private String address;

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
     * ZIP code of the vendor's location.
     */
    @Column(name = "zip_code")
    private String zipCode;

    /**
     * Email address of the company associated with the vendor.
     */
    @Column(name = "company_email")
    private String companyEmail;

    /**
     * Account number associated with the vendor.
     */
    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "remit_address")
    private String remitAddress;

    /**
     * The state of the customer's address.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "remit_state_id")
    private State remitState;
    /**
     * The country of the customer's address.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "remit_country_id")
    private Country remitCountry;

    private String remitZipCode;

    private String remitEmailAddress;

    /**
     * First name of the primary contact person associated with the vendor.
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Last name of the primary contact person associated with the vendor.
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Phone number of the sales representative associated with the vendor.
     */
    @Column(name = "sales_rep_phone_number")
    private String salesRepPhoneNumber;

    /**
     * Email address of the sales representative associated with the vendor.
     */
    @Column(name = "sales_rep_email")
    private String salesRepEmail;

    /**
     * Note or additional information about the sales representative associated with the vendor.
     */
    @Column(name = "sales_rep_note")
    private String salesRepNote;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Inventory> inventoryList;

    Vendor(Integer id, String vendorName, String companyPhoneNumber, String website, String address,
           Integer stateId, String stateName,
           Integer countryId, String countryName,
           String zipCode, String companyEmail, String accountNumber, String remitAddress,
           Integer remitStateId, String remitStateName,
           Integer remitCountryId, String remitCountryName,
           String remitZipCode, String remitEmailAddress,
           String firstName, String lastName, String salesRepPhoneNumber, String salesRepEmail,
           String salesRepNote, Integer userId, String userFirstName, String userLastName) {
        this.id = id;
        this.vendorName = vendorName;
        this.companyPhoneNumber = companyPhoneNumber;
        this.website = website;
        this.address = address;
        this.state = State.builder().id(stateId).name(stateName).build();
        this.country = Country.builder().id(countryId).name(countryName).build();
        this.zipCode = zipCode;
        this.companyEmail = companyEmail;
        this.accountNumber = accountNumber;
        this.remitAddress = remitAddress;
        this.remitState = State.builder().id(remitStateId).name(remitStateName).build();
        this.remitCountry = Country.builder().id(remitCountryId).name(remitCountryName).build();
        this.remitZipCode = remitZipCode;
        this.remitEmailAddress = remitEmailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salesRepPhoneNumber = salesRepPhoneNumber;
        this.salesRepEmail = salesRepEmail;
        this.salesRepNote = salesRepNote;
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build();
    }

    Vendor(Integer id, String vendorName) {
        this.id = id;
        this.vendorName = vendorName;
    }
}
