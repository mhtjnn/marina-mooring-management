package com.marinamooringmanagement.service;


import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.request.UserRequestDto;
import com.marinamooringmanagement.response.UserResponseDto;

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
    public List<UserResponseDto> getAllUser(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

    /**
     * Saves a new user or updates an existing user.
     * @param employee The user request DTO
     * @return A message indicating the result of the operation
     */
    public String saveUser(UserRequestDto employee);

    /**
     * Deletes a user by their ID.
     * @param empId The user ID to delete
     */
    void deleteUser(Integer empId);

    /**
     * Updates an existing user.
     * @param employee The user request DTO
     */
    public void updateUser(UserRequestDto employee);
    /**
     * Finds a user by their email address.
     * @param email The email address of the user to find
     * @return UserDto object if found, null otherwise
     */
    public UserDto findByEmailAddress(String email);
}
