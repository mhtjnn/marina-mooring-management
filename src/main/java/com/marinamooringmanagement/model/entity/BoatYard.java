package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a BoatYard.
 * This class is used to map BoatYard objects to database entities.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BoatYard {
    /**
     * The unique identifier of the BoatYard.
     */
    @Id
    @GeneratedValue(strategy =GenerationType.AUTO)
    private Integer id;
    /**
     * The boatyard ID of the BoatYard.
     */
    @Column(name="Boatyard_Id")
    private String boatyardId;
    /**
     * The mooring name of the BoatYard.
     */
    @Column(name="Mooring_Name")
    private String mooringName;
    /**
     * The owner name of the BoatYard.
     */
    @Column(name="Owner_Name")
    private String ownerName;
    /**
     * The email address of the BoatYard owner.
     */
    @Column(name="Email_Address")
    private String emailAddress;
    /**
     * The phone number of the BoatYard.
     */
    @Column(name="Phone")
    private String phone;


}
