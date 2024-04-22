package com.marinamooringmanagement.model.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a response for a Work Order.
 * This DTO is used for sending Work Order data back to the client as a response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderResponseDto {

    /**
     * The name of the customer associated with the work order.
     */
    private String customerName;
    /**
     * The mooring number related to the work order.
     */
    private String mooringId;
    /**
     * The ID of the customer associated with the Work Order.
     */
    private String customerId;
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
