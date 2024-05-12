package com.marinamooringmanagement.service;


import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.request.UserSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.FetchUsersResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface for User Service.
 * This interface defines methods related to user management.
 */
public interface UserService {

    /**
     * Fetches users based on the provided search criteria.
     *
     * @param userSearchRequest An instance of {@code UserSearchRequest} containing the search criteria.
     * @param customerAdminId   The ID of the customer admin.
     * @param request           The HTTP servlet request.
     * @return A {@code BasicRestResponse} object containing the response data, including the list of users matching the search criteria.
     * @throws IllegalArgumentException if {@code userSearchRequest} is {@code null}.
     * @apiNote The implementation of this method should handle various search criteria specified in the {@code userSearchRequest} and return the appropriate response.
     * @implSpec This method should be implemented to interact with the backend system or data source to fetch users based on the provided search criteria.
     * @implNote Implementations of this method should adhere to the contract defined by {@code BasicRestResponse} for representing REST API responses.
     * @see UserSearchRequest
     * @see BasicRestResponse
     */
    public BasicRestResponse fetchUsers(final UserSearchRequest userSearchRequest, final Integer customerAdminId, final String searchText);

    /**
     * Saves a new user or updates an existing user.
     *
     * @param userRequestDto The user request DTO.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    public BasicRestResponse saveUser(final UserRequestDto userRequestDto, final Integer customerAdminId);

    /**
     * Deletes a user from the database.
     *
     * @param userId  The ID of the user to be deleted.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    BasicRestResponse deleteUser(final Integer userId, final Integer customerAdminId);

    /**
     * Updates an existing user.
     *
     * @param userRequestDto The user request DTO.
     * @param userId         The ID of the user to be updated
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    public BasicRestResponse updateUser(final UserRequestDto userRequestDto, final Integer userId, final Integer customerAdminId);

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
}
