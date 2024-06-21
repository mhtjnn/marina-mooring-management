package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Entity class representing a BoatYard.
 * This class is used to map BoatYard objects to database entities.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "boatyard")
public class Boatyard extends Base {
    /**
     * The unique identifier of the BoatYard.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    /**
     * The boatyard ID of the BoatYard.
     */
    @Column(name = "boatyard_id")
    private String boatyardId;

    /**
     * The name of the BoatYard.
     */
    @Column(name = "boatyard_name")
    private String boatyardName;
    
    /**
     * The address associated with an entity, such as a customer or location.
     */
    @Column(name = "street")
    private String street;

    /**
     * The apartment number of the BoatYard.
     */
    @Column(name = "apt")
    private String apt;

    /**
     * The state associated with the BoatYard.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private State state;

    /**
     * The country associated with the BoatYard.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    /**
     * The zip code of the BoatYard location.
     */
    @Column(name = "zipcode")
    private String zipCode;

    /**
     * The main contact detail for reaching the primary contact person associated with this entity.
     */
    @Column(name = "main_contact")
    private String mainContact;

    @Column(name = "gps_coordinates")
    private String gpsCoordinates;

    /**
     * The list of moorings associated with the BoatYard.
     */
    @OneToMany(mappedBy = "boatyard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Mooring> mooringList;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
