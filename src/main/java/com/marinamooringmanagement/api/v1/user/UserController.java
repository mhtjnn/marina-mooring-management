package com.marinamooringmanagement.api.v1.user;

import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.UserResponseDto;
import com.marinamooringmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Rest Controller for managing {@link com.marinamooringmanagement.model.entity.User} entities.
 */
@RestController
@RequestMapping(value = "/api/v1/user")
@Validated
@CrossOrigin
public class UserController extends GlobalExceptionHandler {
    @Autowired
    private UserService userService;

    /**
     * Fetches a paginated list of users from the database.
     * <p>
     * This REST API endpoint retrieves all users, supports pagination, and allows sorting by specified attributes.
     *
     * @param pageNumber      Page number for pagination, default is 0.
     * @param pageSize        Number of records per page, default is 20.
     * @param sortBy          Attribute name to sort the users, default is "id".
     * @param sortDir         Direction of sorting, can be either "asc" for ascending or "desc" for descending, default is "asc".
     * @param searchText      Optional parameter for searching users by text.
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
    @RequestMapping(
            value = "/",
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "auth")
    public BasicRestResponse fetchUsers(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort By(field to be compared for sorting)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Direction(asc --> ascending and dsc --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "searchText", required = false) final String searchText,
            final HttpServletRequest request
            ) {
        BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return userService.fetchUsers(baseSearchRequest, searchText, request);
    }

    /**
     * Saves a new user in the database.
     * <p>
     * This endpoint is used to create a new user with the details provided in the request body.
     *
     * @param user            {@link UserRequestDto} containing the user details to be saved.
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
    @SecurityRequirement(name = "auth")
    @RequestMapping(
            value = "/",
            method = RequestMethod.POST,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse saveUser(
            @Parameter(description = "User to save", schema = @Schema(implementation = UserRequestDto.class)) final @Valid @RequestBody UserRequestDto user,
            final HttpServletRequest request
    ) {
        return userService.saveUser(user, request);
    }

    /**
     * Updates an existing user in the database.
     * <p>
     * This endpoint allows updating user details for an existing user identified by the information provided in the request body.
     *
     * @param userId ID of the user to update.
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
    @SecurityRequirement(name = "auth")
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse updateUser(
            @Parameter(description = "User ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer userId,
            @Parameter(description = "Fields to update in the user", schema = @Schema(implementation = UserRequestDto.class)) final @Valid @RequestBody UserRequestDto userRequestDto,
            final HttpServletRequest request
    ) {
        return userService.updateUser(userRequestDto, userId, request);
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
    @SecurityRequirement(name = "auth")
    @RequestMapping(
            value = "/{userId}",
            method = RequestMethod.DELETE,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse deleteUser(
            @Parameter(description = "User ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("userId") Integer userId,
            @RequestParam(value = "customerAdminId", required = false) final Integer customerAdminId,
            final HttpServletRequest request
    ) {
        return userService.deleteUser(userId, request);
    }

}
