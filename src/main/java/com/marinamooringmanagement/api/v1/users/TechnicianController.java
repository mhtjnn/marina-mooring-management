package com.marinamooringmanagement.api.v1.users;
import com.marinamooringmanagement.model.dto.TechnicianDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.TechnicianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Controller class for managing technician-related endpoints.
 */
@RestController
@Validated
@RequestMapping(value = "/api/v1/technician")
public class TechnicianController {

    @Autowired
    private TechnicianService technicianService;

    /**
     * Endpoint for saving a new technician.
     *
     * @param technicianDto The DTO containing technician information.
     * @return A BasicRestResponse indicating the success of the operation.
     */

    @Operation(
            tags = "Save technician in the database",
            description = "API to save technician in the database",
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
    public BasicRestResponse saveTechnician(@Valid @RequestBody TechnicianDto technicianDto) {
        final BasicRestResponse res = new BasicRestResponse();
        res.setStatus(HttpStatus.CREATED.value());
        res.setMessage("Technician created successfully");
        technicianService.saveTechnician(technicianDto);
        return res;

    }

    /**
     * Endpoint for retrieving a list of technicians.
     *
     * @param pageNumber The page number for pagination.
     * @param pageSize   The page size for pagination.
     * @param sortBy     The field to sort by.
     * @param sortDir    The direction of sorting.
     * @return A list of TechnicianDto objects.
     */

    @Operation(
            tags = "Fetch technician from the database",
            description = "API to fetch technician from the database",
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
    public List<TechnicianDto> getTechnicians(
            @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "technicianId", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return technicianService.getTechnicians(pageNumber, pageSize, sortBy, sortDir);
    }

    /**
     * Endpoint for retrieving a technician by ID.
     *
     * @param id The ID of the technician.
     * @return The TechnicianDto object corresponding to the given ID.
     */
    @GetMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public TechnicianDto getTechnician(@PathVariable(value = "id") Integer id) {
        return this.technicianService.getbyId(id);
    }

    /**
     * Endpoint for deleting a technician by ID.
     *

     * @param id       The ID of the technician to delete.
     * @return A BasicRestResponse indicating the success of the operation.
     */

    @Operation(
            tags = "Delete technician from the database",
            description = "API to delete technician from the database",
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
    public BasicRestResponse deleteTechnician(@PathVariable(value = "id") Integer id) {
        final BasicRestResponse res = new BasicRestResponse();
        technicianService.deletebyId(id);
        res.setStatus(HttpStatus.OK.value());
        res.setMessage("Technician Deleted");
        return res;
    }

    @Operation(
            tags = "Update technician in the database",
            description = "API to update technician in the database",
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
    public BasicRestResponse updateTechnician(
            @PathVariable(value = "id",required = true) Integer id,
            @Valid @RequestBody TechnicianDto technicianDto,
            HttpServletRequest request, HttpServletResponse response
    ){
        final BasicRestResponse res = new BasicRestResponse();
        technicianService.updateTechnician(technicianDto,id);
        res.setStatus(HttpStatus.OK.value());
        res.setMessage("Technician Updated successfully ");
        return  res;
    }
}


