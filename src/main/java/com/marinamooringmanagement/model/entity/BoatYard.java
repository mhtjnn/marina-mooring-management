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
     * The name of the BoatYard.
     */
    @Column(name="boatYard_name")
    private String boatYardName;

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

    /**
     * The address associated with an entity, such as a customer or location.
     */
    @Column(name="address")
    private String address;
    /**
     * The main contact detail, typically a name or a primary phone number,
     * for reaching the primary contact person associated with this entity.
     */
    @Column(name="main_contact")
    private String mainContact;

}
