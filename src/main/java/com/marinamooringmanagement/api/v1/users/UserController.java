package com.marinamooringmanagement.api.v1.users;

import com.marinamooringmanagement.constants.Authority;

import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.UserResponseDto;
import com.marinamooringmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Rest Controller for managing {@link com.marinamooringmanagement.model.entity.User} entities.
 */
@RestController
@RequestMapping(value = "/api/v1/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Fetches a paginated list of users from the database.
     * <p>
     * This REST API endpoint retrieves all users, supports pagination, and allows sorting by specified attributes.
     *
     * @param pageNumber Page number for pagination, default is 0.
     * @param pageSize   Number of records per page, default is 20.
     * @param sortBy     Attribute name to sort the users, default is "email".
     * @param sortDir    Direction of sorting, can be either "asc" for ascending or "desc" for descending, default is "asc".
     * @return A {@link BasicRestResponse} containing a list of {@link UserResponseDto} representing the users.
     */
    @PreAuthorize(Authority.USER)
    @RequestMapping(
            value = "/",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse getUsers(
            @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "email", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        List<UserResponseDto> userList = userService.getAllUser(pageNumber, pageSize, sortBy, sortDir);
        final BasicRestResponse response = BasicRestResponse.builder().content(userList).message("List of Saved Users in database").status(HttpStatus.OK.value()).build();
        return response;
    }

    /**
     * Saves a new user in the database.
     * <p>
     * This endpoint is used to create a new user with the details provided in the request body.
     *
     * @param user {@link UserRequestDto} containing the user details to be saved.
     * @return A {@link BasicRestResponse} indicating the outcome of the save operation.
     */
    @RequestMapping(
            value = "/",
            method = RequestMethod.POST,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse saveUser(
            @Valid @RequestBody UserRequestDto user
    ) {
        final BasicRestResponse response = new BasicRestResponse();
        response.setMessage(userService.saveUser(user));
        response.setStatus(HttpStatus.CREATED.value());
        return response;
    }

    /**
     * Updates an existing user in the database.
     * <p>
     * This endpoint allows updating user details for an existing user identified by the information provided in the request body.
     *
     * @param userRequestDto {@link UserRequestDto} containing updated user details.
     * @return A {@link BasicRestResponse} indicating the outcome of the update operation.
     */
    @RequestMapping(
            value = "/",
            method = RequestMethod.PUT,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse updateUser(
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        return userService.updateUser(userRequestDto);
    }

    /**
     * Deletes a user from the database.
     * <p>
     * This endpoint removes a user from the database, identified by the user ID provided as a path variable.
     *
     * @param userId ID of the user to be deleted.
     * @return A {@link BasicRestResponse} indicating the outcome of the delete operation.
     */
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.DELETE,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse deleteUser(
            @PathVariable("userId") Integer userId
    ) {
        final BasicRestResponse response = new BasicRestResponse();
        userService.deleteUser(userId);
        response.setMessage("User Deleted Successfully!!!");
        response.setStatus(HttpStatus.OK.value());
        return response;
    }
}