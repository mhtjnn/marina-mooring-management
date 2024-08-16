package com.marinamooringmanagement.api.v1.user;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.constants.Authority;
import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.ImageRequestDto;
import com.marinamooringmanagement.model.request.MultipleImageRequestDto;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.UserResponseDto;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.ImageService;
import com.marinamooringmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Rest Controller for managing {@link com.marinamooringmanagement.model.entity.User} entities.
 */
@RestController
@RequestMapping(value = "/api/v1/user")
@Validated
@CrossOrigin
@Tag(name = "User Controller", description = "These are API's for user.")
public class UserController extends GlobalExceptionHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

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
            summary = "API to fetch users from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse fetchUsers(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort By(field to be compared for sorting)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "companyName", required = false) String sortBy,
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

    @Operation(
            summary = "API to fetch users of technician role from the database",
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
            value = "/fetchTechnicians",
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "auth")
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse fetchTechnicians(
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
        return userService.fetchUsersOfTechnicianRole(baseSearchRequest, searchText, request);
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
            summary = "API to save user in the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
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
            summary = "API to update user in the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
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
            summary = "API to delete user from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse deleteUser(
            @Parameter(description = "User ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("userId") Integer userId,
            final HttpServletRequest request
    ) {
        return userService.deleteUser(userId, request);
    }

    @Operation(
            summary = "API to upload image for a user in the database",
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
    @PutMapping(
            value = "/uploadImage/{userId}",
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.FINANCE + " or " + Authority.TECHNICIAN)
    public BasicRestResponse uploadImage(
            @Parameter(description = "User ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("userId") Integer userId,
            @Parameter(description = "Image Details", schema = @Schema(implementation = ImageRequestDto.class)) final @RequestBody ImageRequestDto imageRequestDto,
            final HttpServletRequest request
    ) {
        Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();
        if(ObjectUtils.notEqual(userId, loggedInUserId)) {
            BasicRestResponse response = BasicRestResponse.builder().build();
            response.setMessage(String.format("Not authorized to upload image for different user as Given id: %1$s is different from logged-in user Id: %2$s", userId, loggedInUserId));
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setTime(new Timestamp(System.currentTimeMillis()));
            return response;
        }
        MultipleImageRequestDto multipleImageRequestDto = MultipleImageRequestDto.builder().imageRequestDtoList(List.of(imageRequestDto)).build();
        return imageService.uploadImage(userId, AppConstants.EntityConstants.USER, multipleImageRequestDto, request);
    }

}
