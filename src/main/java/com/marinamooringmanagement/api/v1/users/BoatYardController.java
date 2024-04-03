package com.marinamooringmanagement.api.v1.users;

import com.marinamooringmanagement.model.dto.BoatYardDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.BoatYardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Controller class for managing Boat Yard operations.
 */
@RestController
@RequestMapping(value = "/api/v1/boatyard")
public class BoatYardController {

    @Autowired
    private BoatYardService boatYardService;

    /**
     * Endpoint for creating a new Boat Yard.
     *
     * @param boatYardDto The BoatYardDto containing the details of the Boat Yard to be created.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @PostMapping(value = "/",
            produces = {"application/json"})
    public BasicRestResponse saveCustomer(@RequestBody BoatYardDto boatYardDto
                                          ) {
        final BasicRestResponse res = new BasicRestResponse();
        res.setStatus(HttpStatus.CREATED.value());
        res.setMessage("BoatYard created successfully");
        boatYardService.saveBoatYard(boatYardDto);
        return res;
    }

    /**
     * Endpoint for retrieving a list of Boat Yards.
     *
     * @param pageNumber The page number of the results (default: 0).
     * @param pageSize   The size of each page (default: 10).
     * @param sortBy     The field to sort by (default: boatyardId).
     * @param sortDir    The direction of sorting (default: asc).
     * @return A list of BoatYardDto objects.
     */
    @GetMapping(value = "/",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public List<BoatYardDto> getUsers(
            @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "boatyardId", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return boatYardService.getBoatYard(pageNumber, pageSize, sortBy, sortDir);
    }

    /**
     * Endpoint for retrieving a Boat Yard by its ID.
     *
     * @param id The ID of the Boat Yard to retrieve.
     * @return The BoatYardDto object corresponding to the given ID.
     */
    @GetMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public
    BoatYardDto getBoatYard(@PathVariable(value = "id") Integer id) {
        return this.boatYardService.getbyId(id);
    }

    /**
     * Endpoint for deleting a Boat Yard by its ID.
     *
     * @param id The ID of the Boat Yard to delete.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @DeleteMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public
    BasicRestResponse deleteBoatYard(@PathVariable(value = "id") Integer id) {
        final BasicRestResponse res = new BasicRestResponse();
        boatYardService.deletebyId(id);
        res.setStatus(HttpStatus.OK.value());
        res.setMessage("BoatYard Deleted ");
        return res;
    }

    /**
     * Endpoint for updating a Boat Yard.
     *
     * @param id           The ID of the Boat Yard to update.
     * @param boatYardDto The BoatYardDto containing the updated details.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @PutMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse updateBoatYard(
            @PathVariable(value = "id",required = true) Integer id,
            @RequestBody BoatYardDto boatYardDto
    ){
        final BasicRestResponse res = new BasicRestResponse();
        boatYardService.updateBoatYard(boatYardDto,id);
        res.setStatus(HttpStatus.OK.value());
        res.setMessage("BoatYard Updated successfully ");
        return  res;
    }




}
