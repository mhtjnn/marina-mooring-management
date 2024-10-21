package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.State;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "address")
    private String address;

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

    @Column(name = "storage_areas")
    private List<String> storageAreas;

    /**
     * The list of moorings associated with the BoatYard.
     */
    @OneToMany(mappedBy = "boatyard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Mooring> mooringList;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "boatyard", fetch = FetchType.LAZY)
    private List<WorkOrder> workOrderList;

    public Boatyard(Integer id, String boatyardId, String boatyardName) {
        this.id = id;
        this.boatyardId = boatyardId;
        this.boatyardName = boatyardName;
    }

    public Boatyard(Integer id, String boatyardName, String boatyardId, Integer userId, String userFirstName, String userLastName) {
        this.id = id;
        this.boatyardName = boatyardName;
        this.boatyardId = boatyardId;
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build();
    }

    public Boatyard(Integer id, String boatyardName, String boatyardId, String address,
                    String gpsCoordinates, String mainContact, String zipCode, List<String> storageAreas,
                    Integer stateId, String stateName,
                    Integer countryId, String countryName,
                    Integer userId, String userFirstName, String userLastName) {
        this.id = id;
        this.boatyardName = boatyardName;
        this.boatyardId = boatyardId;
        this.address = address;
        this.gpsCoordinates = gpsCoordinates;
        this.mainContact = mainContact;
        this.zipCode = zipCode;
        this.storageAreas = storageAreas;
        this.state = State.builder().id(stateId).name(stateName).build();
        this.country = Country.builder().id(countryId).name(countryName).build();
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build();
    }
}
