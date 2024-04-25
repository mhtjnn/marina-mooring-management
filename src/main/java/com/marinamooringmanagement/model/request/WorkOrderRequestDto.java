package com.marinamooringmanagement.model.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a request to create or update a Work Order.
 * This DTO is used for sending Work Order data to the backend for processing.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderRequestDto {
    /**
     * The unique identifier for the work order.
     */
    private Integer id;

    /**
     * The ID of the customer associated with the Work Order.
     */
    private String customerId;
    /**
     * The name of the customer associated with the work order.
     */
    private String customerName;
    /**
     * The mooring number related to the work order.
     */
    private String mooringId;
    /**
     * The boatyard associated with the work order.
     */
    private String boatYard;
    /**
     * The person or team assigned to the work order.
     */
    private String assignedTo;
    /**
     * The due date for completing the work order.
     */
    private String dueDate;
    /**
     * The scheduled date for the work order.
     */
    private String scheduleDate;
    /**
     * The status of the work order.
     */
    private String status;
    /**
     * The time duration or estimated time for completing the work order.
     */
    private String time;
    /**
     * Description of the problem or task reported in the work order.
     */
    private String reportProblem;
}
