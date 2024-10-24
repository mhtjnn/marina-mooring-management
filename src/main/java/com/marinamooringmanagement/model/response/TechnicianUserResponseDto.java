package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Data
@Builder
public class TechnicianUserResponseDto implements Serializable {

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
    private String street;

    /**
     * The apartment or unit number.
     */
    private String apt;

    /**
     * The ZIP code.
     */
    private String zipCode;

    private String companyName;

    private Integer openWorkOrder;

    private Integer closeWorkOrder;

}
