package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderPayStatus;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;
import java.util.List;

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

    @Column(name = "work_order_number")
    private String workOrderNumber;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "schedule_date")
    private Date scheduledDate;

    @Column(name = "completed_date")
    private Date completedDate;

    @Column(name = "time")
    private Time time;

    @Column(name = "problem")
    private String problem;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "mooring_id")
    private Mooring mooring;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "technician_user_id")
    private User technicianUser;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "finance_user_id")
    private User financeUser;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_owner_user_id")
    private User customerOwnerUser;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "work_order_status_id")
    private WorkOrderStatus workOrderStatus;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "work_order_pay_status_id")
    private WorkOrderPayStatus workOrderPayStatus;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "work_order_image_id")
    private List<Image> imageList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "workOrder",fetch = FetchType.EAGER)
    @JsonManagedReference
    private WorkOrderInvoice workOrderInvoice;

}
