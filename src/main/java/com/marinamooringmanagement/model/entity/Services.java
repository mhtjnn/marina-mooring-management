package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a service provided at a marina, including details about the mooring, technician, and service performed.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Services {
    /**
     * The unique identifier for the service.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    /**
     * The name or identifier of the mooring location where the service was provided.
     */
    @Column(name = "mooring")
    private String mooring;
    /**
     * The name of the technician who performed the service.
     */
    @Column(name = "technician_name")
    private String techniciansName;
    /**
     * The amount charged for the service.
     */
    @Column(name = "amount")
    private int amount;
    /**
     * A record or description of the service provided.
     */
    @Column(name = "service_record")
    private String serviceRecord;
}
