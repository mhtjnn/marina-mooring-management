package com.marinamooringmanagement.api.v1.users;

import com.marinamooringmanagement.constants.Authority;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.request.UserSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.UserResponseDto;
import com.marinamooringmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Fetches a paginated list of users from the database.
     * <p>
     * This REST API endpoint retrieves all users, supports pagination, and allows sorting by specified attributes.
     *
     * @param pageNumber Page number for pagination, default is 0.
     * @param pageSize Number of records per page, default is 20.
     * @param sortBy Attribute name to sort the users, default is "email".
     * @param sortDir Direction of sorting, can be either "asc" for ascending or "desc" for descending, default is "asc".
     * @return A {@link BasicRestResponse} containing a list of {@link UserResponseDto} representing the users.
     */
    @Operation(
            tags = "Fetch users from the database",
            description = "API to fetch users from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @PreAuthorize(Authority.ADMINISTRATOR)
    @RequestMapping(
            value = "/",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "auth")
    public BasicRestResponse fetchUsers(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort By(field to be compared for sorting)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "email", required = false) String sortBy,
            @Parameter(description = "Sort Direction(asc --> ascending and dsc --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        UserSearchRequest userSearchRequest = UserSearchRequest.builder().build();
        userSearchRequest.setPageNumber(pageNumber);
        userSearchRequest.setPageSize(pageSize);
        userSearchRequest.setSort(new BaseSearchRequest().getSort(sortBy, sortDir));
        return userService.fetchUsers(userSearchRequest);
    }

    /**
     * Saves a new user in the database.
     * <p>
     * This endpoint is used to create a new user with the details provided in the request body.
     *
     * @param user {@link UserRequestDto} containing the user details to be saved.
     * @return A {@link BasicRestResponse} indicating the outcome of the save operation.
     */
    @Operation(
            tags = "Save user in the database",
            description = "API to save user in the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(
            value = "/",
            method = RequestMethod.POST,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse saveUser(
            @Parameter(description = "User to save", schema = @Schema(implementation = UserRequestDto.class)) final @Valid @RequestBody UserRequestDto user
    ) {
        return userService.saveUser(user);
    }

    /**
     * Updates an existing user in the database.
     * <p>
     * This endpoint allows updating user details for an existing user identified by the information provided in the request body.
     *
     * @param userRequestDto {@link UserRequestDto} containing updated user details.
     * @return A {@link BasicRestResponse} indicating the outcome of the update operation.
     */
    @Operation(
            tags = "Update user in the database",
            description = "API to update user in the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse updateUser(
            @Parameter(description = "User ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer userId,
            @Parameter(description = "Fields to update in the user", schema = @Schema(implementation = UserRequestDto.class)) final @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        return userService.updateUser(userRequestDto, userId);
    }

    /**
     * Deletes a user from the database.
     * <p>
     * This endpoint removes a user from the database, identified by the user ID provided as a path variable.
     *
     * @param userId ID of the user to be deleted.
     * @return A {@link BasicRestResponse} indicating the outcome of the delete operation.
     */
    @Operation(
            tags = "Delete user from the database",
            description = "API to delete user from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.DELETE,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse deleteUser(
            @Parameter(description = "User ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("userId") Integer userId
    ) {
        return userService.deleteUser(userId);
    }
}