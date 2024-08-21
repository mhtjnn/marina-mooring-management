package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.response.config.ConfigResponseDto;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import lombok.*;

import java.io.Serializable;

/**
 * Response DTO (Data Transfer Object) class for {@link com.marinamooringmanagement.model.entity.User}.
 * This class is used for transferring user-related response data between layers of the application.
 */
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Data
@Builder
public class UserResponseDto implements Serializable {

    private static final long serialVersionUID = 1234534567890L;

    /**
     * The unique identifier for the user.
     */
    private Integer id;

    /**
     * The first name of the user.
     */
    private String firstName;

    private String lastName;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    /**
     * The ID of the customer admin associated with the user.
     */
    private Integer customerOwnerId;

    /**
     * The role associated with the user.
     */
    private RoleResponseDto roleResponseDto;

    /**
     * The state associated with the user.
     */
    private StateResponseDto stateResponseDto;

    /**
     * The country associated with the user.
     */
    private CountryResponseDto countryResponseDto;

    /**
     * The street address.
     */
    private String address;
    /**
     * The ZIP code.
     */
    private String zipCode;

    private String companyName;

    private ImageResponseDto imageResponseDto;

    private ConfigResponseDto configResponseDto;
}
