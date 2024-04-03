package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

/**
 * Entity class representing a Technician.
 */
public class Technician {
    /**
     * The unique identifier of the technician.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    /**
     * The name of the technician.
     */
    @Column(name="Technician_Name")
    private String technicianName;
    /**
     * The technician ID.
     */
    @Column(name="TechnicianId")
    private String technicianId;
    /**
     * The email address of the technician.
     */
    @Column(name="EmailAddress")
    private String emailAddress;
    /**
     * The phone number of the technician.
     */
    @Column(name="Phone")
    private String phone;
    /**
     * The street and house number of the technician's address.
     */
    @Column(name="Street/House")
    private String streetHouse;
    /**
     * The sector or block of the technician's address.
     */
    @Column(name="Sector/Block")
    private String sectorBlock;
    /**
     * The state of the technician's address.
     */
    @Column(name="State")
    private String state;
    /**
     * The country of the technician's address.
     */
    @Column(name="Country")
    private String country;
    /**
     * The pin code of the technician's address.
     */
    @Column(name="Pincode")
    private String pincode;
    /**
     * Additional notes or comments about the technician.
     */
    @Column(name="Note")
    private String note;
}
