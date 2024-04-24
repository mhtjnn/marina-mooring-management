package com.marinamooringmanagement.service;


import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.NewPasswordResponse;
import com.marinamooringmanagement.model.response.SendEmailResponse;

import java.util.List;

/**
 * Interface for User Service.
 * This interface defines methods related to user management.
 */
public interface UserService {
    /**
     * Retrieves a paginated and sorted list of users.
     * @param pageNumber The page number
     * @param pageSize The page size
     * @param sortBy The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return List of UserResponseDto objects
     */
    public BasicRestResponse fetchUsers(final Integer pageNumber, final Integer pageSize, final String sortBy, final String sortDir);

    /**
     * Saves a new user or updates an existing user.
     * @param employee The user request DTO
     * @return A message indicating the result of the operation
     */
    public BasicRestResponse saveUser(final UserRequestDto employee);

    /**
     * Deletes a user by their ID.
     * @param empId The user ID to delete
     */
    BasicRestResponse deleteUser(final Integer empId);

    /**
     * Updates an existing user.
     * @param userRequestDto The user request DTO
     */
    public BasicRestResponse updateUser(final UserRequestDto userRequestDto, final Integer userId);
    /**
     * Finds a user by their email address.
     * @param email The email address of the user to find
     * @return UserDto object if found, null otherwise
     */
    public UserDto findByEmailAddress(final String email);

    /**
     * Check validity of the token
     * @param token Reset Password Token
     * @return {@link SendEmailResponse}
     */
    BasicRestResponse checkEmailAndTokenValid(final String token);

    /**
     * Update password for the user having email as subject of the token.
     * @param token Reset Password Token
     * @param newPasswordRequest Newly given password by the user
     * @return {@link NewPasswordResponse}
     * @throws Exception
     */
    BasicRestResponse updatePassword(final String token, final NewPasswordRequest newPasswordRequest) throws Exception;
}
