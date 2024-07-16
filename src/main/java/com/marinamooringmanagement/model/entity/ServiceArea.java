package com.marinamooringmanagement.model.entity;

import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.ServiceAreaType;
import com.marinamooringmanagement.model.entity.metadata.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_area_type_id")
    private ServiceAreaType serviceAreaType;

    @Column(name = "street_house")
    private String streetHouse;

    /**
     * The Apt/Suite of the customer's address.
     */
    @Column(name = "apt_suite")
    private String aptSuite;

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

    @Column(name = "notes")
    private String notes;

}
