package com.marinamooringmanagement.api.v1.customer;

import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.entity.Base;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Controller class for managing customer-related endpoints.
 */
@RestController
@Validated
@RequestMapping(value = "/api/v1/customer")
@Tag(name = "CustomerController", description = "To perform operations on Customer")
@CrossOrigin
public class CustomerController extends Base {


    @Autowired
    private CustomerService customerService;

    /**
     * Endpoint for saving a new customer.
     *
     * @param customerRequestDto The DTO containing customer information.
     * @return A BasicRestResponse indicating the success of the operation.
     */

    @Operation(
            tags = "Save Customer in the database",
            description = "API to save customer in the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @PostMapping(value = "/",
            produces = {"application/json"})
    public BasicRestResponse saveCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto
    ) {
        return customerService.saveCustomer(customerRequestDto);
    }

    /**
     * Endpoint for retrieving a list of customers.
     *
     * @param pageNumber The page number for pagination.
     * @param pageSize   The page size for pagination.
     * @param sortBy     The field to sort by.
     * @param sortDir    The direction of sorting.
     * @return A list of CustomerDto objects.
     */

    @Operation(
            tags = "Fetch customer from the database",
            description = "API to fetch customer from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @GetMapping(value = "/",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchCustomers(
            final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            final @RequestParam(value = "sortBy", defaultValue = "customerId", required = false) String sortBy,
            final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            final @RequestParam(value = "searchText", required = false) String searchText
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return customerService.fetchCustomers(baseSearchRequest, searchText);
    }

    /**
     * Retrieves a customer along with their moorings based on the provided customer ID.
     *
     * @param customerId The name of the customer to fetch along with their moorings.
     * @return A BasicRestResponse containing the customer details and their associated moorings.
     * @throws IllegalArgumentException If the customer ID is null or negative.
     */
    @Operation(
            tags = "Fetch customer and moorings associated with it from the database",
            description = "Fetch customer and moorings associated with it from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @GetMapping(value = "/fetchCustomerWithMoorings/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchCustomerWithMoorings(
            @PathVariable("id") final Integer customerId
    ) {
        return customerService.fetchCustomerAndMoorings(customerId);
    }

    /**
     * Endpoint for retrieving a customer by ID.
     *
     * @param id The ID of the customer.
     * @return The CustomerDto object corresponding to the given ID.
     */
    @GetMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public CustomerDto getCustomer(@PathVariable(value = "id") Integer id) {
        return this.customerService.getById(id);
    }

    /**
     * Endpoint for updating a customer.
     *
     * @param customerRequestDto The DTO containing updated customer information.
     */
    @Operation(
            tags = "Update customers in the database",
            description = "API to update customers in the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @PutMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse updateCustomer(
            @PathVariable(value = "id", required = true) Integer id,
            @Valid @RequestBody CustomerRequestDto customerRequestDto

    ) {

        return customerService.updateCustomer(customerRequestDto, id);
    }

    /**
     * Endpoint for deleting a customer by ID.
     *
     * @param id The ID of the customer to delete.
     * @return A BasicRestResponse indicating the success of the operation.
     */

    @Operation(
            tags = "Delete customer from the database",
            description = "API to delete customer from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @DeleteMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse deleteCustomer(@PathVariable(value = "id") Integer id) {

        return customerService.deleteCustomerById(id);
    }
}