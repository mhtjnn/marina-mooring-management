package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "work_order")
public class WorkOrder extends Base{

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

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "mooring_id")
    private Mooring mooring;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_user_id")
    private User technicianUser;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_owner_user_id")
    private User customerOwnerUser;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_status_id")
    private WorkOrderStatus workOrderStatus;
}
