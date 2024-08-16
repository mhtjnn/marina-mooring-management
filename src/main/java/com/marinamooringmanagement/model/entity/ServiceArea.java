package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.ServiceAreaType;
import com.marinamooringmanagement.model.entity.metadata.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_area")
public class ServiceArea extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "service_area_name")
    private String serviceAreaName;

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

    @Column(name = "zipcode")
    private String zipCode;

    @Column(name = "gps_coordinates")
    private String gpsCoordinates;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_area_type_id")
    private ServiceAreaType serviceAreaType;

    @OneToMany(mappedBy = "serviceArea", cascade = {}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Mooring> mooringList;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "notes")
    private String notes;

    public ServiceArea(Integer id, String serviceAreaName) {
        this.id = id;
        this.serviceAreaName = serviceAreaName;
    }

    public ServiceArea(Integer id, String serviceAreaName, String address,
                       Integer stateId, String stateName,
                       Integer countryId, String countryName,
                       String zipCode, String gpsCoordinates,
                       Integer serviceAreaTypeId, String serviceAreaTypeName,
                       Integer userId, String userFirstName, String userLastName, String notes) {
        this.id = id;
        this.serviceAreaName = serviceAreaName;
        this.address = address;
        this.state = State.builder().id(stateId).name(stateName).build();
        this.country = Country.builder().id(countryId).name(countryName).build();
        this.zipCode = zipCode;
        this.gpsCoordinates = gpsCoordinates;
        this.serviceAreaType = ServiceAreaType.builder().id(serviceAreaTypeId).type(serviceAreaTypeName).build();
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build();
        this.notes = notes;
    }

    public ServiceArea(Integer id, String serviceAreaName,
                       Integer userId, String userFirstName, String userLastName) {
        this.id = id;
        this.serviceAreaName = serviceAreaName;
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build();
    }

}
