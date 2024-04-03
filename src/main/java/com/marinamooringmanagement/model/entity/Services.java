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
    @Column(name="Mooring")
    private String mooring;
    @Column(name="Technician_Name")
    private String techniciansName;
    @Column(name="Amount")
    private int amount;
    @Column(name="ServiceRecord")
    private String serviceRecord;
}
