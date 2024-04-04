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
public class BoatYard extends Base {
    /**
     * The unique identifier of the BoatYard.
     */
    @Id
    @GeneratedValue(strategy =GenerationType.AUTO)
    private Integer id;
    /**
     * The boatyard ID of the BoatYard.
     */
    @Column(name="boatyard_id")
    private String boatyardId;
    /**
     * The mooring name of the BoatYard.
     */
    @Column(name="mooring_name")
    private String mooringName;
    /**
     * The owner name of the BoatYard.
     */
    @Column(name="owner_name")
    private String ownerName;
    /**
     * The email address of the BoatYard owner.
     */
    @Column(name="email_address")
    private String emailAddress;
    /**
     * The phone number of the BoatYard.
     */
    @Column(name="phone")
    private String phone;


}
