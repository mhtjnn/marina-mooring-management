package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a mooring entity that maps to the "mooring" table in the database.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mooring")
public class Mooring extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * Name of the customer associated with the mooring.
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * Mooring number assigned to the mooring.
     */
    @Column(name = "mooring_number")
    private String mooringNumber;

    /**
     * Harbor where the mooring is located.
     */
    @Column(name = "harbor")
    private String harbor;

    /**
     * Water depth at the mooring location.
     */
    @Column(name = "water_depth")
    private String waterDepth;

    /**
     * GPS coordinates of the mooring.
     */
    @Column(name = "gps_coordinates")
    private String gpsCoordinates;

    /**
     * Name of the boat associated with the mooring.
     */
    @Column(name = "boat_name")
    private String boatName;

    /**
     * Size of the boat associated with the mooring.
     */
    @Column(name = "boat_size")
    private String boatSize;

    /**
     * Type of the boat associated with the mooring.
     */
    @Column(name = "boat_type")
    private String boatType;

    /**
     * Weight of the boat associated with the mooring.
     */
    @Column(name = "boat_weight")
    private String boatWeight;

    /**
     * Size unit of the boat weight.
     */
    @Column(name = "size_of_weight")
    private String sizeOfWeight;

    /**
     * Type of the boat weight.
     */
    @Column(name = "type_of_weight")
    private String typeOfWeight;

    /**
     * Condition of the eye related to the mooring.
     */
    @Column(name = "condition_of_eye")
    private String conditionOfEye;

    /**
     * Condition of the top chain related to the mooring.
     */
    @Column(name = "top_chain_condition")
    private String topChainCondition;

    /**
     * Condition of the bottom chain related to the mooring.
     */
    @Column(name = "bottom_chain_condition")
    private String bottomChainCondition;

    /**
     * Condition of the shackle/swivel related to the mooring.
     */
    @Column(name = "shackle_swivel_condition")
    private String shackleSwivelCondition;

    /**
     * Condition of the pennant related to the mooring.
     */
    @Column(name = "pennant_condition")
    private String pennantCondition;

    /**
     * Depth at mean high water at the mooring location.
     */
    @Column(name = "depth_at_high_water")
    private Integer depthAtMeanHighWater;
}

