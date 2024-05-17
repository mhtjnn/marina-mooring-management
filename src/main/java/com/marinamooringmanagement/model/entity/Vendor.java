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
     * Name of the company associated with the vendor.
     */
    @Column(name = "company_name")
    private String companyName;

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
    @Column(name = "street")
    private String street;

    /**
     * Apartment or suite number of the vendor.
     */
    @Column(name = "apt_suite")
    private String aptSuite;

    /**
     * State where the vendor is located.
     */
    @Column(name = "state")
    private String state;

    /**
     * Country where the vendor is located.
     */
    @Column(name = "country")
    private String country;

    /**
     * ZIP code of the vendor's location.
     */
    @Column(name = "zip_code")
    private Integer zipCode;

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

    /**
     * Flag indicating whether the sales representative associated with the vendor is primary or not.
     */
    @Column(name = "primary_sales_rep")
    private boolean primarySalesRep;
}
