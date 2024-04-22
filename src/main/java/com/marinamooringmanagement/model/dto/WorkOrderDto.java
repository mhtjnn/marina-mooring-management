package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a Work Order with essential details.
 * Inherits from BaseDto for common fields like ID and creation/update timestamps.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderDto extends BaseDto {
    /**
     * The name of the customer associated with the work order.
     */
    private String customerName;
    /**
     * The mooring number related to the work order.
     */
    private String mooringNumber;
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
     * The status of the work order (e.g., in progress, completed, etc.).
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
