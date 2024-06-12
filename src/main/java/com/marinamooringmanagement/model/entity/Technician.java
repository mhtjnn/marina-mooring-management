package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity class representing a Technician.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "technician")
public class Technician extends Base {
    /**
     * The unique identifier of the technician.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    /**
     * The name of the technician.
     */
    @Column(name = "technician_name")
    private String technicianName;
    /**
     * The technician ID.
     */
    @Column(name = "technician_id")
    private String technicianId;
    /**
     * The email address of the technician.
     */
    @Column(name = "email_address")
    private String emailAddress;
    /**
     * The phone number of the technician.
     */
    @Column(name = "phone")
    private String phone;
    /**
     * The street and house number of the technician's address.
     */
    @Column(name = "street_house")
    private String streetHouse;
    /**
     * The sector or block of the technician's address.
     */
    @Column(name = "sector_block")
    private String sectorBlock;
    /**
     * The state of the technician's address.
     */
    @Column(name = "state")
    private String state;
    /**
     * The country of the technician's address.
     */
    @Column(name = "country")
    private String country;
    /**
     * The pin code of the technician's address.
     */
    @Column(name = "zipcode")
    private String zipcode;
    /**
     * Additional notes or comments about the technician.
     */
    @Column(name = "note")
    private String note;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = {}, mappedBy = "technician", fetch = FetchType.LAZY)
    private List<WorkOrder> workOrderList;
}
