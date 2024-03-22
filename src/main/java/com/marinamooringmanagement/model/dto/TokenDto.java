package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO (Data Transfer Object) class representing Token entity data.
 * This class extends BaseDto and includes token-specific attributes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1234567890L; // Update with an appropriate value

    /**
     * The unique identifier for the token.
     */
    private Integer id;

    /**
     * The token value.
     */
    private String token;

    /**
     * The expiration date and time of the token.
     */
    private Date expire_at;

    /**
     * The ID of the associated employee.
     */
    private String employee_id;
}

