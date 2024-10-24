package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "estimate")
public class Estimate extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "schedule_date")
    private Date scheduledDate;

    @Column(name = "time")
    private Time time;

    @Column(name = "problem")
    private String problem;

    @Column(name = "cost")
    private BigDecimal cost;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "mooring_id")
    private Mooring mooring;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "boatyard_id")
    private Boatyard boatyard;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_user_id")
    private User technicianUser;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_owner_user_id")
    private User customerOwnerUser;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_status_id")
    private WorkOrderStatus workOrderStatus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estimate", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Inventory> inventoryList;

}
