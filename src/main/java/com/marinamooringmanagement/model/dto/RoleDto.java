package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO (Data Transfer Object) class representing Role entity data.
 * This class extends BaseDto and includes role-specific attributes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = -388497461004331133L;

    private Integer id;

    /**
     * The name of the role.
     */
    private String name;

    /**
     * The description of the role.
     */
    private String description;
}

