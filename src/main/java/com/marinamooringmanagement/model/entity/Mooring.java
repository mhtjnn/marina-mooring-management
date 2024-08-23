package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.marinamooringmanagement.model.entity.metadata.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
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
    private String mooringNumber;

    /**
     * Harbor where the mooring is located.
     */
    @Column(name = "harbor")
    private String harborOrArea;

    /**
     * GPS coordinates of the mooring.
     */
    @Column(name = "gps_coordinates")
    private String gpsCoordinates;

    @Column(name = "install_bottom_chain_date")
    private Date installBottomChainDate;

    @Column(name = "install_top_chain_date")
    private Date installTopChainDate;

    @Column(name = "install_condition_of_eye_date")
    private Date installConditionOfEyeDate;

    @Column(name = "inspection_date")
    private Date inspectionDate;

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

    @Column(name = "boat_registration")
    private String boatRegistration;

    /**
     * Weight of the boat associated with the mooring.
     */
    @Column(name = "boat_weight")
    private String boatWeight;

    /**
     * Size unit of the boat weight.
     */
    private Integer sizeOfWeight;

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
    private String pendantCondition;

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

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    @ToString.Exclude
    private Customer customer;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "boatyard_id")
    @JsonBackReference
    @ToString.Exclude
    private Boatyard boatyard;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mooring", fetch = FetchType.LAZY)
    private List<Image> imageList;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_area_id")
    @JsonBackReference
    @ToString.Exclude
    private ServiceArea serviceArea;

    public Mooring(Integer id, String mooringNumber, String harborOrArea, String gpsCoordinates,
                   Date installBottomChainDate, Date installTopChainDate, Date installConditionOfEyeDate,
                   Date inspectionDate, String boatName, String boatSize,
                   Integer boatTypeId, String boatTypeName,
                   String boatWeight,
                   Integer sizeOfWeight,
                   Integer typeOfWeightId, String typeOfWeightName,
                   Integer eyeConditionId, String eyeConditionName,
                   Integer topChainConditionId, String topChainConditionName,
                   Integer bottomChainConditionId, String bottomChainConditionName,
                   Integer shackleSwivelConditionId, String shackleSwivelConditionName,
                   String pendantCondition, Integer depthAtMeanHighWater,
                   Integer mooringStatusId, String mooringStatusName,
                   Integer customerId, String customerFirstName, String customerLastName, String customerNumber,
                   Integer userId, String userFirstName, String userLastName,
                   Integer boatyardId, String boatyardName,
                   Integer serviceAreaId, String serviceAreaName)
    {
        this.id = id;
        this.mooringNumber = mooringNumber;
        this.harborOrArea = harborOrArea;
        this.gpsCoordinates = gpsCoordinates;
        this.installBottomChainDate = installBottomChainDate;
        this.installTopChainDate = installTopChainDate;
        this.installConditionOfEyeDate = installConditionOfEyeDate;
        this.inspectionDate = inspectionDate;
        this.boatName = boatName;
        this.boatSize = boatSize;
        this.boatType = BoatType.builder().id(boatTypeId).boatType(boatTypeName).build();
        this.boatWeight = boatWeight;
        this.sizeOfWeight = sizeOfWeight;
        this.typeOfWeight = TypeOfWeight.builder().id(typeOfWeightId).type(typeOfWeightName).build();
        this.eyeCondition = EyeCondition.builder().id(eyeConditionId).condition(eyeConditionName).build();
        this.topChainCondition = TopChainCondition.builder().id(topChainConditionId).condition(topChainConditionName).build();
        this.bottomChainCondition = BottomChainCondition.builder().id(bottomChainConditionId).condition(bottomChainConditionName).build();
        this.shackleSwivelCondition = ShackleSwivelCondition.builder().id(shackleSwivelConditionId).condition(shackleSwivelConditionName).build();
        this.pendantCondition = pendantCondition;
        this.depthAtMeanHighWater = depthAtMeanHighWater;
        this.mooringStatus = MooringStatus.builder().id(mooringStatusId).status(mooringStatusName).build();
        this.customer = Customer.builder().id(customerId).firstName(customerFirstName).lastName(customerLastName).customerId(customerNumber).build();
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build();
        this.boatyard = Boatyard.builder().id(boatyardId).boatyardName(boatyardName).build();
        this.serviceArea = ServiceArea.builder().id(serviceAreaId).serviceAreaName(serviceAreaName).build();
    }

    public Mooring(Integer id, String mooringNumber, String harborOrArea, String gpsCoordinates,
                   Date installBottomChainDate, Date installTopChainDate, Date installConditionOfEyeDate,
                   Date inspectionDate, String boatName, String boatSize, Integer boatTypeId, String boatTypeName,
                   String boatWeight,
                   Integer sizeOfWeight, Integer typeOfWeightId, String typeOfWeightName, Integer eyeConditionId,
                   String eyeConditionName, Integer topChainConditionId, String topChainConditionName,
                   Integer bottomChainConditionId, String bottomChainConditionName, Integer shackleSwivelConditionId,
                   String shackleSwivelConditionName, String pendantCondition, Integer depthAtMeanHighWater,
                   Integer mooringStatusId, String mooringStatusName, Integer customerId, String customerFirstName,
                   String customerLastName,
                   String customerNumber, Integer userId, String userFirstName, String userLastName,
                   Integer boatyardId,
                   String boatyardName, Integer serviceAreaId,
                   String serviceAreaName, List<Image> imageList)
    {
        this.id = id;
        this.mooringNumber = mooringNumber;
        this.harborOrArea = harborOrArea;
        this.gpsCoordinates = gpsCoordinates;
        this.installBottomChainDate = installBottomChainDate;
        this.installTopChainDate = installTopChainDate;
        this.installConditionOfEyeDate = installConditionOfEyeDate;
        this.inspectionDate = inspectionDate;
        this.boatName = boatName;
        this.boatSize = boatSize;
        this.boatType = BoatType.builder().id(boatTypeId).boatType(boatTypeName).build();
        this.boatWeight = boatWeight;
        this.sizeOfWeight = sizeOfWeight;
        this.typeOfWeight = TypeOfWeight.builder().id(typeOfWeightId).type(typeOfWeightName).build();
        this.eyeCondition = EyeCondition.builder().id(eyeConditionId).condition(eyeConditionName).build();
        this.topChainCondition = TopChainCondition.builder().id(topChainConditionId).condition(topChainConditionName).build();
        this.bottomChainCondition = BottomChainCondition.builder().id(bottomChainConditionId).condition(bottomChainConditionName).build();
        this.shackleSwivelCondition = ShackleSwivelCondition.builder().id(shackleSwivelConditionId).condition(shackleSwivelConditionName).build();
        this.pendantCondition = pendantCondition;
        this.depthAtMeanHighWater = depthAtMeanHighWater;
        this.mooringStatus = MooringStatus.builder().id(mooringStatusId).status(mooringStatusName).build();
        this.customer = Customer.builder().id(customerId).firstName(customerFirstName).lastName(customerLastName).customerId(customerNumber).build();
        this.user = User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build();
        this.boatyard = Boatyard.builder().id(boatyardId).boatyardName(boatyardName).build();
        this.serviceArea = ServiceArea.builder().id(serviceAreaId).serviceAreaName(serviceAreaName).build();
        this.imageList = imageList;
    }

    public Mooring(Integer id, String mooringNumber) {
        this.id = id;
        this.mooringNumber = mooringNumber;
    }

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
