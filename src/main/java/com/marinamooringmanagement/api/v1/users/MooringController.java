package com.marinamooringmanagement.api.v1.users;

import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import com.marinamooringmanagement.service.MooringService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * REST Controller for handling mooring-related operations.
 */
@RestController
@RequestMapping("/api/v1/mooring")
@Validated
public class MooringController {

    @Autowired
    private MooringService mooringService;

    /**
     * Fetches a list of moorings based on pagination and sorting parameters.
     *
     * @param page    Page number for pagination (default: 1)
     * @param size    Page size for pagination (default: 10)
     * @param sortBy  Field to sort by (default: "id")
     * @param sortDir Sorting direction (asc/desc, default: "asc")
     * @return BasicRestResponse containing the fetched moorings
     */
    @RequestMapping(
            value = "/",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    public BasicRestResponse fetchMoorings(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer page,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        List<MooringResponseDto> mooringResponseDtoList = mooringService.fetchMoorings(page, size, sortBy, sortDir);

        response.setMessage("All moorings fetched successfully.");
        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.OK.value());
        response.setContent(mooringResponseDtoList);

        return response;
    }

    /**
     * Saves a new mooring.
     *
     * @param dto Request DTO containing mooring details
     * @return BasicRestResponse indicating the status of the operation
     */
    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = {"application/json"})
    public BasicRestResponse saveMooring(
            @Valid @RequestBody MooringRequestDto dto
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        mooringService.saveMooring(dto);

        response.setMessage("Mooring saved successfully.");
        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    /**
     * Updates an existing mooring.
     *
     * @param dto       Request DTO containing updated mooring details
     * @param mooringId ID of the mooring to be updated
     * @return BasicRestResponse indicating the status of the operation
     */
    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            produces = {"application/json"})
    public BasicRestResponse updateMooring(
            @Valid @RequestBody MooringRequestDto dto,
            @PathVariable("id") Integer mooringId
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        mooringService.updateMooring(dto, mooringId);

        response.setMessage("Mooring updated successfully");
        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    /**
     * Deletes a mooring by ID.
     *
     * @param id ID of the mooring to be deleted
     * @return BasicRestResponse indicating the status of the deletion operation
     */
    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {"application/json"})
    public BasicRestResponse deleteMooring(
            @PathVariable("id") Integer id
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        final String message = mooringService.deleteMooring(id);

        response.setMessage(message);
        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.OK.value());

        return response;
    }
}
