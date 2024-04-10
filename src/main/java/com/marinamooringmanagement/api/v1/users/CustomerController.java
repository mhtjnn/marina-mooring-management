package com.marinamooringmanagement.api.v1.users;
import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.entity.Base;
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

@Tag(name="CustomerController",description = "To perform operations on Customer")
public class CustomerController extends Base {


    @Autowired
    private CustomerService customerService;

    /**
     * Endpoint for saving a new customer.
     *
     * @param customerDto The DTO containing customer information.
     * @return A BasicRestResponse indicating the success of the operation.
     */

    @Operation(
            tags = "Save Customer in the database",
            description = "API to save customer in the database",
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
    @PostMapping(value = "/",
            produces = {"application/json"})
    public BasicRestResponse saveCustomer( @Valid @RequestBody CustomerDto customerDto
                                                   ) {
        final BasicRestResponse res = new BasicRestResponse();
        res.setStatus(HttpStatus.CREATED.value());
        res.setMessage("Customer created successfully");
        customerService.saveCustomer(customerDto);
        return res;


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

    @GetMapping(value = "/",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse getCustomers(
            @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "customerId", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return customerService.getCustomers(pageNumber, pageSize, sortBy, sortDir);
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
    public
    CustomerDto getCustomer(@PathVariable(value = "id") Integer id) {
        return this.customerService.getbyId(id);
    }

    /**
     * Endpoint for updating a customer.
     *
     * @param customerDto The DTO containing updated customer information.

     */
    @Operation(
            tags = "Update customers in the database",
            description = "API to update customers in the database",
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
    @PutMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse updateCustomer(
            @PathVariable(value = "id",required = true) Integer id,
            @Valid @RequestBody CustomerDto customerDto

    ){
        final BasicRestResponse res = new BasicRestResponse();
        customerService.updateCustomer(customerDto,id);
        res.setStatus(HttpStatus.OK.value());
        res.setMessage("Customer Updated successfully ");
        return  res;
    }

    /**
     * Endpoint for deleting a customer by ID.
     *
     * @param id       The ID of the customer to delete.
     * @return A BasicRestResponse indicating the success of the operation.
     */

    @Operation(
            tags = "Delete customer from the database",
            description = "API to delete customer from the database",
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
    @DeleteMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public
    BasicRestResponse deleteCustomer(@PathVariable(value = "id") Integer id) {
        final BasicRestResponse res = new BasicRestResponse();

        customerService.deletebyId(id);
        res.setStatus(HttpStatus.OK.value());
        res.setMessage("Customer Deleted");
        return res;
    }
}




