package com.marinamooringmanagement.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a Work Order in the system.
 * Extends Base class for common fields like ID and creation/update timestamps.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder extends  Base{
    /**
     * The unique identifier for the work order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    /**
     * The name of the customer associated with the work order.
     */
    @Column(name="customer_name")
    private String customerName;
    /**
     * The mooring number related to the work order.
     */
    @Column(name = "mooring_number")
    private String mooringNumber;
    /**
     * The boatyard associated with the work order.
     */
    @Column(name="boatYard")
    private String boatYard;
    /**
     * The person or team assigned to the work order.
     */
    @Column(name="assigned_to")
    private String assignedTo;
    /**
     * The due date for completing the work order.
     */
    @Column(name = "due_date")
    private String dueDate;
    /**
     * The scheduled date for the work order.
     */
    @Column(name="schedule_date")
    private String scheduleDate;
    /**
     * The status of the work order (e.g., in progress, completed, etc.).
     */
    @Column(name="status")
    private String status;
    /**
     * The time duration or estimated time for completing the work order.
     */
    @Column(name = "time")
    private String time;
    /**
     * Description of the problem or task reported in the work order.
     */
    @Column(name = "report_problem")
    private String reportProblem;

}
