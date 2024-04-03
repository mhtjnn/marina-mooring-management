package com.marinamooringmanagement.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a mooring data transfer object (DTO) used for transferring mooring-related information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 5502680630355079L;

    /**
     * Unique identifier for the mooring.
     */
    private Integer id;

    /**
     * Unique identifier assigned to the mooring.
     */
    private String mooringId;

    /**
     * Name of the mooring.
     */
    @NotNull(message = "Mooring name can't be blank")
    private String mooringName;

    /**
     * Name of the owner associated with the mooring.
     */
    private String ownerName;

    /**
     * Start date of mooring reservation.
     */
    private Date fromDate;

    /**
     * End date of mooring reservation.
     */
    private Date toDate;

    /**
     * Latitude coordinate of the mooring location.
     */
    private String latitude;

    /**
     * Longitude coordinate of the mooring location.
     */
    private String longitude;

    /**
     * Status of the mooring (e.g., available, reserved, occupied).
     */
    private String status;
}
