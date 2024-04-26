package com.marinamooringmanagement.model.request;

import com.marinamooringmanagement.model.dto.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a search request for querying user information based on specified criteria.
 *
 * <p>This class extends {@code BaseSearchRequest} and includes additional fields to define search criteria for users.
 *
 * @see BaseSearchRequest
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchRequest extends BaseSearchRequest{
    /**
     * The unique identifier for the user.
     */
    private Integer id;

    /**
     * The first name of the user.
     */
    private String firstname;

    /**
     * The last name of the user.
     */
    private String lastname;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * The role associated with the user.
     */
    private RoleDto role;
}