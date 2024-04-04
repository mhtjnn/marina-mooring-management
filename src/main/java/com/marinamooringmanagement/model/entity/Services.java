package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Services {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name="mooring")
    private String mooring;
    @Column(name="technician_name")
    private String techniciansName;
    @Column(name="amount")
    private int amount;
    @Column(name="service_record")
    private String serviceRecord;
}
