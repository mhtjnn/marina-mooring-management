package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

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

    @Column(name = "mooring_id")
    private String mooringId;

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
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "boat_type_id")
    private BoatType boatType;

    /**
     * Weight of the boat associated with the mooring.
     */
    @Column(name = "boat_weight")
    private String boatWeight;

    /**
     * Size unit of the boat weight.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "size_of_weight_id")
    private SizeOfWeight sizeOfWeight;

    /**
     * Type of the boat weight.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_of_weight_id")
    private TypeOfWeight typeOfWeight;

    /**
     * Condition of the eye related to the mooring.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "eye_condition_id")
    private EyeCondition eyeCondition;

    /**
     * Condition of the top chain related to the mooring.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "top_chain_condition_id")
    private TopChainCondition topChainCondition;

    /**
     * Condition of the bottom chain related to the mooring.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "bottom_chain_condition_id")
    private BottomChainCondition bottomChainCondition;

    /**
     * Condition of the shackle/swivel related to the mooring.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn (name = "shackle_swivel_condition_id")
    private ShackleSwivelCondition shackleSwivelCondition;

    /**
     * Condition of the pennant related to the mooring.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "pennant_condition_id")
    private PennantCondition pennantCondition;

    /**
     * Depth at mean high water at the mooring location.
     */
    @Column(name = "depth_at_high_water")
    private Integer depthAtMeanHighWater;

    /**
     * Status of the mooring.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private MooringStatus mooringStatus;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "boatyard_id")
    @JsonBackReference
    private Boatyard boatyard;

    @OneToOne(mappedBy = "mooring", cascade = {}, fetch = FetchType.LAZY)
    private WorkOrder workOrder;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mooring)) return false;
        Mooring mooring = (Mooring) o;
        return Objects.equals(id, mooring.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
