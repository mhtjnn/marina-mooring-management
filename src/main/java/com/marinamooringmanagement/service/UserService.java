package com.marinamooringmanagement.service;


import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface for User Service.
 * This interface defines methods related to user management.
 */
public interface UserService {

    /**
     * Fetches a list of users based on the provided search request parameters, customer admin ID, and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText the text used to search for specific users by name, email, role, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the user search.
     */
    public BasicRestResponse fetchUsers(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    /**
     * Saves a new user or updates an existing user.
     *
     * @param userRequestDto The user request DTO.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    public BasicRestResponse saveUser(final UserRequestDto userRequestDto, final HttpServletRequest request);

    /**
     * Deletes a user from the database.
     *
     * @param userId  The ID of the user to be deleted.
     * @param request
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    BasicRestResponse deleteUser(final Integer userId, final HttpServletRequest request);

    /**
     * Updates an existing user.
     *
     * @param userRequestDto The user request DTO.
     * @param userId         The ID of the user to be updated.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    public BasicRestResponse updateUser(final UserRequestDto userRequestDto, final Integer userId, final HttpServletRequest request);

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user to find.
     * @return UserDto object if found, null otherwise.
     */
    public UserDto findByEmailAddress(final String email);

    /**
     * Checks the validity of the token.
     *
     * @param token The reset password token.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    BasicRestResponse checkEmailAndTokenValid(final String token);

    /**
     * Updates the password for the user having email as the subject of the token.
     *
     * @param token               The reset password token.
     * @param newPasswordRequest The new password request.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     * @throws Exception If an error occurs during the password update process.
     */
    BasicRestResponse updatePassword(final String token, final NewPasswordRequest newPasswordRequest) throws Exception;

    BasicRestResponse fetchUsersOfTechnicianRole(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);
}

