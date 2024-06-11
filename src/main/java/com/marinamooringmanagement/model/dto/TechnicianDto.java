package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) class representing a Technician.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianDto extends BaseDto {

    private Integer id;
    /**
     * The name of the technician.
     */
    private String technicianName;
    /**
     * The ID of the technician.
     */
    private String technicianId;
    /**
     * The email address of the technician.
     */
    private String emailAddress;
    /**
     * The phone number of the technician.
     */
    private String phone;
    /**
     * The street and house number of the technician's address.
     */
    private String streetHouse;
    /**
     * The sector or block of the technician's address.
     */
    private String sectorBlock;
    /**
     * The state of the technician's address.
     */
    private String state;
    /**
     * The country of the technician's address.
     */
    private String country;
    /**
     * The pin code of the technician's address.
     */
    private String zipcode;
    /**
     * Additional notes or comments about the technician.
     */
    private String note;

    private UserDto userDto;

    private List<WorkOrderDto> workOrderDtoList;
}